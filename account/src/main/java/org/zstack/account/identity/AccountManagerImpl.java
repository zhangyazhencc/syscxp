package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.account.header.identity.APICheckApiPermissionMsg;
import org.zstack.account.header.identity.APICheckApiPermissionReply;
import org.zstack.account.header.identity.APIValidateSessionMsg;
import org.zstack.account.header.identity.APIValidateSessionReply;
import org.zstack.account.header.account.*;
import org.zstack.account.header.user.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.account.APIValidateAccountMsg;
import org.zstack.header.account.APIValidateAccountReply;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.StatementEffect;
import org.zstack.header.identity.AccountType;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.*;
import org.zstack.header.query.QueryOp;
import org.zstack.sms.MailService;
import org.zstack.utils.*;
import org.zstack.utils.logging.CLogger;

import org.zstack.sms.SmsService;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;

import org.zstack.header.identity.*;

import org.zstack.account.header.identity.*;

import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;

/**
 * Created by zxhread on 17/8/3.
 */
public class AccountManagerImpl extends AbstractService implements AccountManager, PrepareDbInitialValueExtensionPoint,
        ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(AccountManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private IdentiyInterceptor identiyInterceptor;
    @Autowired
    private SmsService smsService;
    @Autowired
    MailService mailservice;

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
        identiyInterceptor.checkApiMessagePermission(msg);
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
        } else if (msg instanceof APIGetSessionPolicyMsg) {
            handle((APIGetSessionPolicyMsg) msg);
        } else if (msg instanceof APICheckApiPermissionMsg) {
            handle((APICheckApiPermissionMsg) msg);
        } else if(msg instanceof APIRegisterAccountMsg){
            handle((APIRegisterAccountMsg) msg);
        } else if(msg instanceof APIAccountPWDBackMsg){
            handle((APIAccountPWDBackMsg) msg);
        } else if(msg instanceof APIUserPWDBackMsg){
            handle((APIUserPWDBackMsg) msg);
        } else if(msg instanceof APIVerifyRepetitionMsg){
            handle((APIVerifyRepetitionMsg) msg);
        }else if (msg instanceof APIValidateAccountMsg) {
            handle((APIValidateAccountMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIValidateAccountMsg msg) {
        AccountVO accountVO = dbf.findByUuid(msg.getUuid(),AccountVO.class);
        APIValidateAccountReply reply = new APIValidateAccountReply();
        if(accountVO!=null){
            reply.setValidAccount(true);
        }
        bus.reply(msg, reply);
    }

    private void handle(APIVerifyRepetitionMsg msg) {
        APIVerifyRepetitionReply reply = new APIVerifyRepetitionReply();

        if(msg.getAccountName() != null){
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.name, Op.EQ, msg.getAccountName());
            if(q.isExists()){
                reply.setAccountName(true);
            }
        }

        if(msg.getAccountEmail() != null){
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.email, Op.EQ, msg.getAccountEmail());
            if(q.isExists()){
                reply.setAccountEmail(true);
            }
        }
        if(msg.getAccountPhone() != null){
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.phone, Op.EQ, msg.getAccountPhone());
            if(q.isExists()){
                reply.setAccountPhone(true);
            }
        }
        if(msg.getUserName() != null){
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(UserVO_.name, Op.EQ, msg.getUserName());
            if(q.isExists()){
                reply.setUserName(true);
            }
        }
        bus.reply(msg, reply);
    }

    private void handle(APIAccountPWDBackMsg msg) {
        APIAccountPWDBackEvent evt = new APIAccountPWDBackEvent(msg.getId());

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[%s, %s]", msg.getPhone(), msg.getCode() ));
        }else{

            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.phone, Op.EQ, msg.getPhone());
            AccountVO account = q.find();
            account.setPassword(msg.getNewpassword());
            evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));
        }

        bus.publish(evt);
    }

    private void handle(APIUserPWDBackMsg msg) {
        APIUserPWDBackEvent evt = new APIUserPWDBackEvent(msg.getId());

        if (smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            SimpleQuery<AccountVO> qa = dbf.createQuery(AccountVO.class);
            qa.add(AccountVO_.name, Op.EQ, msg.getAccountName());
            AccountVO account = qa.find();
            if (account != null) {
                SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
                q.add(UserVO_.accountUuid, Op.EQ, account.getUuid());
                q.add(UserVO_.phone, Op.EQ, msg.getPhone());
                UserVO user = q.find();

                user.setPassword(msg.getNewpassword());
                evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
            }else{
                throw new ApiMessageInterceptionException(argerr("account[%s] is not exists", msg.getAccountName()));
            }
        }else{
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[%s, %s]", msg.getPhone(), msg.getCode() ));
        }

        bus.publish(evt);
    }

    @Transactional
    public void handle(APIRegisterAccountMsg msg) {

        AccountVO accountVO = new AccountVO();
        accountVO.setUuid(Platform.getUuid());
        accountVO.setName(msg.getName());
        accountVO.setPassword(msg.getPassword());
        accountVO.setCompany(msg.getCompany());
        accountVO.setDescription(msg.getDescription());
        accountVO.setEmail(msg.getEmail());
        accountVO.setIndustry(msg.getIndustry());
        accountVO.setPhone(msg.getPhone());
        accountVO.setStatus(AccountStatus.Available);
        accountVO.setType(AccountType.Normal);
        accountVO.setPhoneStatus(ValidateStatus.Validated);
        accountVO.setEmailStatus(ValidateStatus.Unvalidated);


        AccountExtraInfoVO ext = new AccountExtraInfoVO();
        ext.setUuid(accountVO.getUuid());
        ext.setCreateWay("register");
        ext.setGrade(AccountGrade.Normal);

        accountVO.setAccountExtraInfo(ext);

        dbf.getEntityManager().persist(accountVO);

        AccountApiSecurityVO api = new AccountApiSecurityVO();
        api.setUuid(Platform.getUuid());
        api.setAccountUuid(accountVO.getUuid());
        api.setPrivateKey(getRandomString(30));
        api.setPublicKey(getRandomString(16));
        dbf.getEntityManager().persist(api);

        APIRegisterAccountEvent evt = new APIRegisterAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(accountVO));
        bus.publish(evt);
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
                    identiyInterceptor.checkApiMessagePermission(api);
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

        SessionInventory s = identiyInterceptor.getSession(msg.getSessionUuid());
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

        if(valid){
            if(msg.getSession().getAccountUuid().equals(msg.getSession().getUserUuid())){
                reply.setAccountInventory(AccountInventory.valueOf(
                        dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class)
                ));
            }else{
                reply.setUserInventory(UserInventory.valueOf(
                        dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class)
                ));
            }
        }

        bus.reply(msg, reply);
    }


    private void handle(APIGetSessionPolicyMsg msg) {
        APIGetSessionPolicyReply reply = new APIGetSessionPolicyReply();

        SessionInventory s = identiyInterceptor.getSession(msg.getSessionUuid());
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
            } else {
                s = session.toSessionInventory();
                if (s.isUserSession()) {
                    s.setPolicyStatements(identiyInterceptor.getUserPolicyStatements(s.getUserUuid()));
                }
            }
        }

        if (valid) {
            reply.setSessionInventory(s);
        }

        reply.setValidSession(valid);
        bus.reply(msg, reply);
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
            account = dbf.findByUuid(msg.getAccountUuid(), AccountVO.class);
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

        reply.setInventory(identiyInterceptor.initSession(account, user));
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

        reply.setInventory(identiyInterceptor.initSession(vo, null));
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
    @Transactional
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
                vo.setPhoneStatus(ValidateStatus.Validated);
                vo.setEmail(AccountConstant.INITIAL_SYSTEM_ADMIN_EMAIL);
                vo.setEmailStatus(ValidateStatus.Validated);
                vo.setType(AccountType.SystemAdmin);
                vo.setStatus(AccountStatus.Available);

                AccountExtraInfoVO ext = new AccountExtraInfoVO();
                ext.setUuid(vo.getUuid());
                ext.setGrade(AccountGrade.Normal);
                ext.setCreateWay("init");
                vo.setAccountExtraInfo(ext);

                dbf.getEntityManager().persist(vo);

                AccountApiSecurityVO api = new AccountApiSecurityVO();
                api.setUuid(Platform.getUuid());
                api.setAccountUuid(vo.getUuid());
                api.setPrivateKey(getRandomString(30));
                api.setPublicKey(getRandomString(16));
                dbf.getEntityManager().persist(api);

                logger.debug(String.format("Created initial system admin account[name:%s]", AccountConstant.INITIAL_SYSTEM_ADMIN_NAME));
            }
        } catch (Exception e) {
            throw new CloudRuntimeException("Unable to create default system admin account", e);
        }
    }

    @Transactional(readOnly = true)
    public Timestamp getCurrentSqlDate() {
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
        } else if (msg instanceof APIResetAccountPWDMsg) {
            validate((APIResetAccountPWDMsg) msg);
        } else if (msg instanceof APIResetAccountApiSecurityMsg) {
            validate((APIResetAccountApiSecurityMsg) msg);
        } else if (msg instanceof APIGetAccountApiKeyMsg) {
            validate((APIGetAccountApiKeyMsg) msg);
        } else if (msg instanceof APIQueryPolicyMsg) {
            validate((APIQueryPolicyMsg) msg);
        } else if (msg instanceof APIRegisterAccountMsg) {
            validate((APIRegisterAccountMsg) msg);
        }

        setServiceId(msg);

        return msg;
    }

    private void validate(APIRegisterAccountMsg msg) {

        //测试中，去除验证
//        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
//            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
//                    msg.getAccountUuid()));
//        }

    }

    private void validate(APIQueryPolicyMsg msg) {
        if (msg.getSession().getType().equals(AccountType.Proxy))
            msg.addQueryCondition(PolicyVO_.accountType.getName(), QueryOp.IN, "Proxy", "Normal");
        if (msg.getSession().getType().equals(AccountType.Normal))
            msg.addQueryCondition(PolicyVO_.accountType.getName(), QueryOp.IN, "Normal");
    }

    private void validate(APIResetAccountPWDMsg msg) {
        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.accountUuid, Op.EQ, msg.getAccountUuid());
        q.add(ProxyAccountRefVO_.customerAcccountUuid, Op.EQ, msg.getUuid());

        if (msg.getSession().getType() == AccountType.SystemAdmin || q.isExists()) {
        }else{
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot reset the password of the other account[uuid: %s]",
                    msg.getAccountUuid(), msg.getUuid()));
        }
    }

    private void validate(APIResetAccountApiSecurityMsg msg) {

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getAccountUuid()));
        }

    }

    private void validate(APIGetAccountApiKeyMsg msg) {

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getAccountUuid()));
        }

    }

    private void validate(APILogInByUserMsg msg) {
        if (msg.getAccountName() == null && msg.getAccountUuid() == null) {
            throw new ApiMessageInterceptionException(argerr(
                    "accountName and accountUuid cannot both be null, you must specify at least one"
            ));
        }
    }

    private void validate(APIUpdateUserPhoneMsg msg) {
        if(!msg.getOldphone().equals(dbf.findByUuid(msg.getSession().getUserUuid(),
                UserVO.class).getPhone())){
            throw new ApiMessageInterceptionException(argerr("wrong oldphone"));
        }

        if (!smsService.validateVerificationCode(msg.getOldphone(), msg.getOldcode())
                ||!smsService.validateVerificationCode(msg.getNewphone(), msg.getNewcode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateUserPWDMsg msg) {

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateUserEmailMsg msg) {

        if(!msg.getOldEmail().equals(dbf.findByUuid(msg.getSession().getUserUuid(),
                UserVO.class).getEmail())){
            throw new ApiMessageInterceptionException(argerr("wrong oldmail"));
        }

        if (!mailservice.ValidateMailCode(msg.getOldEmail(), msg.getOldCode())||
                !mailservice.ValidateMailCode(msg.getNewEmail(), msg.getNewCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountPWDMsg msg) {
        if(!msg.getPhone().equals(dbf.findByUuid(msg.getAccountUuid(),
                AccountVO.class).getPhone())){
            throw new ApiMessageInterceptionException(argerr("wrong oldphone"));
        }

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountPhoneMsg msg) {
        if(!msg.getOldphone().equals(dbf.findByUuid(msg.getSession().getAccountUuid(),
                AccountVO.class).getPhone())){
            throw new ApiMessageInterceptionException(argerr("wrong oldphone"));
        }

        if (!smsService.validateVerificationCode(msg.getOldphone(), msg.getOldcode())||
                !smsService.validateVerificationCode(msg.getNewphone(), msg.getNewcode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountEmailMsg msg) {

        if(!msg.getOldEmail().equals(dbf.findByUuid(msg.getSession().getAccountUuid(),
                AccountVO.class).getEmail())){
            throw new ApiMessageInterceptionException(argerr("wrong oldmail"));
        }

        if (!mailservice.ValidateMailCode(msg.getOldEmail(), msg.getOldCode())||
                !mailservice.ValidateMailCode(msg.getNewEmail(), msg.getNewCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }


    private void validate(APICreateUserMsg msg) {
        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.accountUuid, Op.EQ, msg.getAccountUuid());
        q.add(UserVO_.name, Op.EQ, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create a user. user name %s is already under the account[uuid:%s]",
                    msg.getName(), msg.getAccountUuid()));
        }
    }

    private void validate(APICreateAccountMsg msg) {
        if (msg.getSession().getType() != AccountType.SystemAdmin
                && msg.getSession().getType() != AccountType.Proxy){
            throw new ApiMessageInterceptionException(argerr("unable to create a account. account[uuid:%s] is not admin or proxy",
                    msg.getSession().getAccountUuid()));
        }

        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        q.add(AccountVO_.name, Op.EQ, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create an account. account name %s already is exsists", msg.getName()));
        }
    }

    private void validate(APIUpdateUserMsg msg) {
        if (msg.getName() != null) {
            UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);

            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.accountUuid, Op.EQ, user.getAccountUuid());
            q.add(UserVO_.name, Op.EQ, msg.getName());
            q.add(UserVO_.uuid, Op.NOT_EQ, user.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("unable to create a user. user name %s is already under the account[uuid:%s]",
                        msg.getName(), msg.getAccountUuid()));
            }
        }
    }

    private void validate(APIUpdateAccountMsg msg) {
        if (AccountType.SystemAdmin.toString().equalsIgnoreCase(msg.getType())){
            throw new OperationFailureException(operr("cannot set account type to SystemAdmin"));
        }

        if (msg.getSession().getType() != AccountType.SystemAdmin &&
                ! msg.getSession().getAccountUuid().equals(msg.getUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot update another account[uuid: %s]",
                    msg.getSession().getAccountUuid(), msg.getUuid()));
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

    private String getRandomString(int length) {
        Random random = new Random();
        String base = "ABCDEFGFHJKMOPQRSTUVWXYZabcdefghjkmnopqrstuvwxy023456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
