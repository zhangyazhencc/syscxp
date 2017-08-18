package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.account.header.identity.APICheckApiPermissionMsg;
import org.zstack.account.header.identity.APICheckApiPermissionReply;
import org.zstack.account.header.identity.APIValidateSessionMsg;
import org.zstack.account.header.identity.APIValidateSessionReply;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.StatementEffect;
import org.zstack.header.identity.AccountType;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.*;
import org.zstack.utils.*;
import org.zstack.utils.logging.CLogger;

import org.zstack.sms.SmsService;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.*;

import org.zstack.header.identity.*;

import org.zstack.account.header.identity.*;
import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;
/**
 * Created by zxhread on 17/8/3.
 * modify by wangwg on 2017/08/16
 */
public class AccountManagerImpl extends AbstractService implements AccountManager, PrepareDbInitialValueExtensionPoint,
        ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(AccountManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private IdentiyInterceptor identiyInterceptor;

    @Autowired
    private SmsService smsService;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof AccountMessage) {
            passThrough((AccountMessage) msg);
        } else if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    @Override
    public void checkApiMessagePermission(APIMessage msg) {
        identiyInterceptor.check(msg);
    }

    @Override
    public boolean isAdmin(SessionInventory session) {
        return session.isAdminAccountSession();
    }

    private void passThrough(AccountMessage msg) {
        AccountVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountVO.class);
        if (vo == null) {
            String err = String.format("unable to find account[uuid=%s]", msg.getAccountUuid());
            bus.replyErrorByMessageType((Message) msg, errf.instantiateErrorCode(SysErrors.RESOURCE_NOT_FOUND, err));
            return;
        }

        AccountBase base = new AccountBase(vo);
        base.handleMessage((Message) msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APILogInByAccountMsg) {
            handle((APILogInByAccountMsg) msg);
        } else if (msg instanceof APILogInByUserMsg) {
            handle((APILogInByUserMsg) msg);
        } else if (msg instanceof APILogOutMsg) {
            handle((APILogOutMsg) msg);
        } else if (msg instanceof APIValidateSessionMsg) {
            handle((APIValidateSessionMsg) msg);
        } else if (msg instanceof APIGetSessionPolicyMsg){
            handle((APIGetSessionPolicyMsg) msg);
        } else if (msg instanceof APICheckApiPermissionMsg) {
            handle((APICheckApiPermissionMsg) msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICheckApiPermissionMsg msg) {
        if (msg.getUserUuid() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.uuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(AccountVO_.type, Op.EQ, AccountType.SystemAdmin);
            boolean isAdmin = q.isExists();

            SimpleQuery<UserVO> uq = dbf.createQuery(UserVO.class);
            uq.add(UserVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            uq.add(UserVO_.uuid, Op.EQ, msg.getUserUuid());
            boolean isMine = uq.isExists();

            if (!isAdmin && !isMine) {
                throw new OperationFailureException(operr(
                        "the user specified by the userUuid[%s] does not belong to the current account, and the" +
                                " current account is not an admin account, so it has no permission to check the user's" +
                                "permissions", msg.getUserUuid()
                ));
            }
        }

        Map<String, String> ret = new HashMap<>();

        SessionInventory session = new SessionInventory();
        if (msg.getUserUuid() != null) {
            UserVO user = dbf.findByUuid(msg.getUserUuid(), UserVO.class);
            session.setAccountUuid(user.getAccountUuid());
            session.setUserUuid(user.getUuid());
        } else {
            session = msg.getSession();
        }

        for (String apiName : msg.getApiNames()) {
            try {
                Class apiClass = Class.forName(apiName);
                APIMessage api = (APIMessage) apiClass.newInstance();
                api.setSession(session);

                try {
                    identiyInterceptor.check(api);
                    ret.put(apiName, StatementEffect.Allow.toString());
                } catch (ApiMessageInterceptionException e) {
                    logger.debug(e.getMessage());
                    ret.put(apiName, StatementEffect.Deny.toString());
                }
            } catch (ClassNotFoundException e) {
                throw new OperationFailureException(argerr("%s is not an API", apiName));
            } catch (Exception e) {
                throw new CloudRuntimeException(e);
            }
        }

        APICheckApiPermissionReply reply = new APICheckApiPermissionReply();
        reply.setInventory(ret);
        bus.reply(msg, reply);
    }


    private void handle(APIValidateSessionMsg msg) {
        APIValidateSessionReply reply = new APIValidateSessionReply();

        SessionInventory s = identiyInterceptor.getSessions().get(msg.getSessionUuid());
        Timestamp current = dbf.getCurrentSqlTime();
        boolean valid = true;

        if (s != null) {
            if (current.after(s.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(s.getUuid());
            }
        } else {
            SessionVO session = dbf.findByUuid(msg.getSessionUuid(), SessionVO.class);
            if (session != null && current.after(session.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(session.getUuid());
            } else if (session == null) {
                valid = false;
            }
        }

        reply.setValidSession(valid);
        bus.reply(msg, reply);
    }


    private void handle(APIGetSessionPolicyMsg msg) {
        APIGetSessionPolicyReply reply = new APIGetSessionPolicyReply();

        SessionInventory s = identiyInterceptor.getSessions().get(msg.getSessionUuid());
        Timestamp current = dbf.getCurrentSqlTime();
        boolean valid = true;

        if (s != null) {
            if (current.after(s.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(s.getUuid());
            }
        } else {
            SessionVO session = dbf.findByUuid(msg.getSessionUuid(), SessionVO.class);
            if (session != null && current.after(session.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(session.getUuid());
            } else if (session == null) {
                valid = false;
            }else{
                s = session.toSessionInventory();
            }
        }

        if (valid){
            SessionPolicyInventory sp = new SessionPolicyInventory();
            sp.setUuid(s.getUuid());
            sp.setAccountUuid(s.getAccountUuid());
            sp.setUserUuid(s.getUserUuid());
            sp.setType(s.getType());
            sp.setCreateDate(s.getCreateDate());
            sp.setExpiredDate(s.getExpiredDate());

            if (s.isUserSession() || s.isAdminUserSession()) {
                List<SessionPolicyInventory.SessionPolicy> policys = new ArrayList<SessionPolicyInventory.SessionPolicy>();
                List<PolicyInventory> userPolicys = getUserPolicys(sp.getUserUuid());
                for (PolicyInventory pi : userPolicys) {
                    SessionPolicyInventory.SessionPolicy p = new SessionPolicyInventory.SessionPolicy();
                    p.setUuid(pi.getUuid());
                    p.setName(pi.getName());
                    p.setStatements(pi.getStatements());
                    policys.add(p);
                }
                sp.setStatements(policys);
            }

            reply.setSessionPolicyInventory(sp);
        }

        reply.setValidSession(valid);
        bus.reply(msg, reply);
    }

    private List<PolicyInventory> getUserPolicys(String userUuid) {
        String sql = "select p from PolicyVO p, UserPolicyRefVO ref where ref.userUuid = :uuid and ref.policyUuid = p.uuid";
        TypedQuery<PolicyVO> q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
        q.setParameter("uuid", userUuid);
        return PolicyInventory.valueOf(q.getResultList());
    }


    private void handle(APILogOutMsg msg) {
        APILogOutReply reply = new APILogOutReply();
        identiyInterceptor.logOutSession(msg.getSessionUuid());
        bus.reply(msg, reply);
    }


    private void handle(APILogInByUserMsg msg) {
        APILogInReply reply = new APILogInReply();

        AccountVO account;
        if (msg.getAccountUuid() != null) {
            account =  dbf.findByUuid(msg.getAccountUuid(), AccountVO.class);
        } else {
            SimpleQuery<AccountVO> accountq = dbf.createQuery(AccountVO.class);
            accountq.add(AccountVO_.name, Op.EQ, msg.getAccountName());
            account = accountq.find();
        }
        if (account == null) {
            throw new OperationFailureException(argerr("account[%s] not found", msg.getAccountName()));
        }

        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.accountUuid, Op.EQ, account.getUuid());
        q.add(UserVO_.password, Op.EQ, msg.getPassword());
        q.add(UserVO_.name, Op.EQ, msg.getUserName());
        UserVO user = q.find();

        if (user == null) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "wrong account or username or password"
            ));
            bus.reply(msg, reply);
            return;
        }

        reply.setInventory(identiyInterceptor.getSession(user.getAccountUuid(), account.getType(), user.getUuid()));
        bus.reply(msg, reply);
    }

    private void handle(APILogInByAccountMsg msg) {
        APILogInReply reply = new APILogInReply();

        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        q.add(AccountVO_.name, Op.EQ, msg.getAccountName());
        q.add(AccountVO_.password, Op.EQ, msg.getPassword());
        AccountVO vo = q.find();
        if (vo == null) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR, "wrong account name or password"));
            bus.reply(msg, reply);
            return;
        }

        reply.setInventory(identiyInterceptor.getSession(vo.getUuid(), vo.getType(), vo.getUuid()));
        bus.reply(msg, reply);
    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(AccountConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        try {
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void prepareDbInitialValue() {
        logger.debug("Created initial system admin account");
        try {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.name, Op.EQ, AccountConstant.INITIAL_SYSTEM_ADMIN_NAME);
            q.add(AccountVO_.type, Op.EQ, AccountType.SystemAdmin);
            if (!q.isExists()) {
                AccountVO vo = new AccountVO();
                vo.setUuid(AccountConstant.INITIAL_SYSTEM_ADMIN_UUID);
                vo.setName(AccountConstant.INITIAL_SYSTEM_ADMIN_NAME);
                vo.setPassword(AccountConstant.INITIAL_SYSTEM_ADMIN_PASSWORD);
                vo.setPhone(AccountConstant.INITIAL_SYSTEM_ADMIN_PHONE);
                vo.setPhoneStatus(AccountAuthentication.YES);
                vo.setEmail(AccountConstant.INITIAL_SYSTEM_ADMIN_EMAIL);
                vo.setEmailStatus(AccountAuthentication.YES);
                vo.setType(AccountType.SystemAdmin);
                vo.setStatus(AccountStatus.Available);
//                vo.setGrade(AccountGrade.Important);

                dbf.persist(vo);
                logger.debug(String.format("Created initial system admin account[name:%s]", AccountConstant.INITIAL_SYSTEM_ADMIN_NAME));
            }
        } catch (Exception e) {
            throw new CloudRuntimeException("Unable to create default system admin account", e);
        }
    }

    @Transactional(readOnly = true)
    private Timestamp getCurrentSqlDate() {
        Query query = dbf.getEntityManager().createNativeQuery("select current_timestamp()");
        return (Timestamp) query.getSingleResult();
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        if (msg instanceof APIUpdateAccountMsg) {
            validate((APIUpdateAccountMsg) msg);
        } else if (msg instanceof APICreateAccountMsg) {
            validate((APICreateAccountMsg) msg);
        } else if (msg instanceof APICreateUserMsg) {
            validate((APICreateUserMsg) msg);
        } else if (msg instanceof APILogInByUserMsg) {
            validate((APILogInByUserMsg) msg);
        } else if (msg instanceof APIUpdateUserPhoneMsg) {
            validate((APIUpdateUserPhoneMsg) msg);
        } else if (msg instanceof APIUpdateUserPWDMsg) {
            validate((APIUpdateUserPWDMsg) msg);
        } else if (msg instanceof APIUpdateUserEmailMsg) {
            validate((APIUpdateUserEmailMsg) msg);
        } else if (msg instanceof APIUpdateAccountPWDMsg) {
            validate((APIUpdateAccountPWDMsg) msg);
        } else if (msg instanceof APIUpdateAccountPhoneMsg) {
            validate((APIUpdateAccountPhoneMsg) msg);
        } else if (msg instanceof APIUpdateAccountEmailMsg) {
            validate((APIUpdateAccountEmailMsg) msg);
        } else if (msg instanceof APIUpdateUserMsg) {
            validate((APIUpdateUserMsg) msg);
        } else if (msg instanceof APIDetachPolicyFromUserMsg) {
            validate((APIDetachPolicyFromUserMsg) msg);
        } else if (msg instanceof APIAttachPolicyToUserMsg) {
            validate((APIAttachPolicyToUserMsg) msg);
        }

        setServiceId(msg);

        return msg;
    }

    private void validate(APILogInByUserMsg msg) {
        if (msg.getAccountName() == null && msg.getAccountUuid() == null) {
            throw new ApiMessageInterceptionException(argerr(
                    "accountName and accountUuid cannot both be null, you must specify at least one"
            ));
        }
    }

    private void validate(APIUpdateUserPhoneMsg msg) {
        if (!smsService.validateVerificationCode(msg.getPhone(),msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateUserPWDMsg msg) {
        if (!smsService.validateVerificationCode(msg.getPhone(),msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateUserEmailMsg msg) {
//        if () {
//            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
//                    msg.getSession().getAccountUuid()));
//        }
    }

    private void validate(APIUpdateAccountPWDMsg msg) {
        if (!smsService.validateVerificationCode(msg.getPhone(),msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountPhoneMsg msg) {
        if (!smsService.validateVerificationCode(msg.getPhone(),msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountEmailMsg msg) {
//        if () {
//            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
//                    msg.getSession().getAccountUuid()));
//        }
    }


    private void validate(APICreateUserMsg msg) {
        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.accountUuid, Op.EQ, msg.getAccountUuid());
        q.add(UserVO_.name, Op.EQ, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create a user. A user called %s is already under the account[uuid:%s]",
                    msg.getName(), msg.getAccountUuid()));
        }
    }

    private void validate(APICreateAccountMsg msg) {
        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        q.add(AccountVO_.name, Op.EQ, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create an account. An account already called %s", msg.getName()));
        }
    }

    private void validate(APIUpdateUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getTargetUuid(), UserVO.class);
        if (!AccountConstant.INITIAL_SYSTEM_ADMIN_UUID.equals(msg.getAccountUuid()) &&
                !user.getAccountUuid().equals(msg.getAccountUuid())) {
            throw new OperationFailureException(argerr("the user[uuid:%s] does not belong to the" +
                    " account[uuid:%s]", user.getUuid(),user.getAccountUuid()));
        }
    }


    private void validate(APIUpdateAccountMsg msg) {
        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);
        if (!account.getType().equals(AccountType.SystemAdmin) &&
                !msg.getSession().getAccountUuid().equals(msg.getTargetUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s, name: %s] is a normal account, it cannot reset the password of another account[uuid: %s]",
                    account.getUuid(), account.getName(), msg.getTargetUuid()));
        }
    }

    private void validate(APIDetachPolicyFromUserMsg msg) {
        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.uuid, Op.EQ, msg.getUserUuid());
        UserVO user = q.find();
        if (!msg.getSession().getType().equals(AccountType.SystemAdmin) &&
                !msg.getSession().getAccountUuid().equals(user.getAccountUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot set the policy of the other user[uuid: %s]",
                    msg.getAccountUuid(), msg.getUserUuid()));
        }
    }

    private void validate(APIAttachPolicyToUserMsg msg) {
        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.uuid, Op.EQ, msg.getUserUuid());
        UserVO user = q.find();
        if (!msg.getSession().getType().equals(AccountType.SystemAdmin) &&
                !msg.getSession().getAccountUuid().equals(user.getAccountUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot set the policy of the other user[uuid: %s]",
                    msg.getAccountUuid(), msg.getUserUuid()));
        }
    }

    private void setServiceId(APIMessage msg) {
        if (msg instanceof AccountMessage) {
            AccountMessage amsg = (AccountMessage) msg;
            bus.makeTargetServiceIdByResourceUuid(msg, AccountConstant.SERVICE_ID, amsg.getAccountUuid());
        }
    }

}
