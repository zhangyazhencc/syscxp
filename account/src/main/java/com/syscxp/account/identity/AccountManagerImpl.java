package com.syscxp.account.identity;

import com.syscxp.account.header.account.*;
import com.syscxp.account.header.identity.*;
import com.syscxp.account.header.user.*;
import com.syscxp.account.quota.UserQuotaOperator;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.SimpleQuery.Op;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.account.*;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.*;
import com.syscxp.header.managementnode.PrepareDbInitialValueExtensionPoint;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.sms.ImageCodeService;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsService;
import com.syscxp.utils.ShellResult;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;
import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by zxhread on 17/8/3.
 */
public class AccountManagerImpl extends AbstractService implements AccountManager, PrepareDbInitialValueExtensionPoint,
        ApiMessageInterceptor, ReportQuotaExtensionPoint {
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
    private MailService mailService;
    @Autowired
    private ImageCodeService imageCodeService;

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
        } else if (msg instanceof APIRegisterAccountMsg) {
            handle((APIRegisterAccountMsg) msg);
        } else if (msg instanceof APIAccountPWDBackByPhoneMsg) {
            handle((APIAccountPWDBackByPhoneMsg) msg);
        } else if (msg instanceof APIAccountPWDBackByEmailMsg) {
            handle((APIAccountPWDBackByEmailMsg) msg);
        } else if (msg instanceof APIUserPWDBackMsg) {
            handle((APIUserPWDBackMsg) msg);
        } else if (msg instanceof APIVerifyRepetitionMsg) {
            handle((APIVerifyRepetitionMsg) msg);
        } else if (msg instanceof APIValidateAccountMsg) {
            handle((APIValidateAccountMsg) msg);
        } else if (msg instanceof APIGetAccountUuidListByProxyMsg) {
            handle((APIGetAccountUuidListByProxyMsg) msg);
        } else if (msg instanceof APIValidateAccountWithProxyMsg) {
            handle((APIValidateAccountWithProxyMsg) msg);
        } else if (msg instanceof APIGetAccountForShareMsg) {
            handle((APIGetAccountForShareMsg) msg);
        } else if (msg instanceof APIListAccountByUuidMsg) {
            handle((APIListAccountByUuidMsg) msg);
        } else if (msg instanceof APIMailOrPhoneIsExistentMsg) {
            handle((APIMailOrPhoneIsExistentMsg) msg);
        } else if (msg instanceof APIGetSecretKeyMsg) {
            handle((APIGetSecretKeyMsg) msg);
        } else if (msg instanceof APILogInBySecretIdMsg) {
            handle((APILogInBySecretIdMsg) msg);
        } else if (msg instanceof APIGetAccountExpiredCleanMsg) {
            handle((APIGetAccountExpiredCleanMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIGetAccountExpiredCleanMsg msg) {

        APIGetAccountExpiredCleanReply reply = new APIGetAccountExpiredCleanReply();

        Optional<AccountVO> accountOptional = Optional.ofNullable(
                dbf.findByUuid(msg.getUuid(), AccountVO.class)
        );

        if (accountOptional.isPresent()) {
            reply.setExpiredClean(accountOptional.get().isExpiredClean());
            reply.setUuid(msg.getUuid());
        } else {
            reply.setError(Platform.argerr("no this account[%s]", msg.getUuid()));
        }

        bus.reply(msg, reply);

    }

    private void handle(APIGetSecretKeyMsg msg) {

        APIGetSecretKeyReply reply = new APIGetSecretKeyReply();

        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.secretId, SimpleQuery.Op.EQ, msg.getSecretId());
        AccountApiSecurityVO vo = q.find();

        if (vo == null || (StringUtils.isNotBlank(vo.getAllowIp()) && !vo.getAllowIp().contains(msg.getIP()))) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Incorrect secretId or secretKey or illegal ip"));
        } else {
            reply.setSecretKey(vo.getSecretKey());
        }

        bus.reply(msg, reply);
    }

    private void handle(APILogInBySecretIdMsg msg) {
        APILogInBySecretIdReply reply = new APILogInBySecretIdReply();

        SessionInventory session = identiyInterceptor.getSessionUuid(msg.getSecretId(), msg.getSecretKey());

        reply.setSession(session);

        bus.reply(msg, reply);
    }

    private void handle(APIMailOrPhoneIsExistentMsg msg) {

        APIMailOrPhoneIsExistentReply reply = new APIMailOrPhoneIsExistentReply();

        if (msg.getAccountMail() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.email, Op.EQ, msg.getAccountMail());
            q.add(AccountVO_.uuid, Op.NOT_EQ, msg.getSession().getAccountUuid());
            if (q.isExists()) {
                reply.setAccountMail(true);
            }
        }
        if (msg.getAccountPhone() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.phone, Op.EQ, msg.getAccountPhone());
            q.add(AccountVO_.uuid, Op.NOT_EQ, msg.getSession().getAccountUuid());
            if (q.isExists()) {
                reply.setAccountPhone(true);
            }
        }
        if (msg.getUserMail() != null) {
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.email, Op.EQ, msg.getUserMail());
            q.add(UserVO_.uuid, Op.NOT_EQ, msg.getSession().getUserUuid());
            if (q.isExists()) {
                reply.setUserMail(true);
            }
        }
        if (msg.getUserPhone() != null) {
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.phone, Op.EQ, msg.getUserPhone());
            q.add(UserVO_.uuid, Op.NOT_EQ, msg.getSession().getUserUuid());
            if (q.isExists()) {
                reply.setUserPhone(true);
            }
        }

        bus.reply(msg, reply);


    }

    private void handle(APIListAccountByUuidMsg msg) {

        APIListAccountByUuidReply reply = new APIListAccountByUuidReply();

        List<Map<String, String>> lists = new ArrayList<>();
        for (String uuid : msg.getAccountUuidList()) {
            Map<String, String> map = new HashMap<>();
            AccountVO vo = dbf.findByUuid(uuid, AccountVO.class);
            if (vo != null) {
                map.put("uuid", vo.getUuid());
                map.put("name", vo.getName());
                map.put("phone", vo.getPhone());
                map.put("company", vo.getCompany());
                lists.add(map);
            }
        }

        reply.setAccountList(lists);
        bus.reply(msg, reply);

    }


    private void handle(APIGetAccountForShareMsg msg) {

        APIGetAccountForShareReply reply = new APIGetAccountForShareReply();

        AccountVO account = null;
        if (msg.getAccountName() != null) {
            account = dbf.createQuery(AccountVO.class).add(AccountVO_.name, SimpleQuery.Op.EQ, msg.getAccountName()).find();
        } else if (msg.getAccountPhone() != null) {
            account = dbf.createQuery(AccountVO.class).add(AccountVO_.phone, SimpleQuery.Op.EQ, msg.getAccountPhone()).find();
        } else {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.ACCOUNT_NOT_FOUND,
                    "params is all null"));
        }

        if (account != null) {
            reply.setName(account.getName());
            reply.setUuid(account.getUuid());
            reply.setPhone(account.getPhone());
            reply.setCompany(account.getCompany());
        } else {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.ACCOUNT_NOT_FOUND,
                    "no find this account"));
        }

        bus.reply(msg, reply);
    }

    private void handle(APIAccountPWDBackByEmailMsg msg) {
        APIAccountPWDBackByEmailEvent evt = new APIAccountPWDBackByEmailEvent(msg.getId());

        if (!mailService.ValidateMailCode(msg.getEmail(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[%s, %s]", msg.getEmail(), msg.getCode()));
        } else {

            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.email, Op.EQ, msg.getEmail());
            AccountVO account = q.find();
            if (account.getType() == AccountType.SystemAdmin) {
                throw new ApiMessageInterceptionException(argerr("SystemAdmin禁止使用找回密码功能[email %s]", msg.getEmail()));
            }
            account.setPassword(msg.getNewPassword());
            account = dbf.updateAndRefresh(account);
            evt.setInventory(AccountInventory.valueOf(account));
        }

        bus.publish(evt);
    }

    private void handle(APIValidateAccountWithProxyMsg msg) {
        APIValidateAccountWithProxyReply reply = new APIValidateAccountWithProxyReply();
        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.customerAccountUuid, Op.EQ, msg.getAccountUuid());
        q.add(ProxyAccountRefVO_.accountUuid, Op.EQ, msg.getProxyUuid());
        ProxyAccountRefVO proxyAccountRefVO = q.find();
        if (proxyAccountRefVO != null) {
            reply.setHasRelativeAccountWithProxy(true);
        }
        bus.reply(msg, reply);
    }


    private void handle(APIValidateAccountMsg msg) {
        AccountVO accountVO = dbf.findByUuid(msg.getUuid(), AccountVO.class);
        APIValidateAccountReply reply = new APIValidateAccountReply();
        if (accountVO != null) {
            reply.setValidAccount(true);
            reply.setType(accountVO.getType());
        }
        reply.setHasProxy(accountHasProxy(msg.getUuid()));
        bus.reply(msg, reply);
    }

    private boolean accountHasProxy(String accountUuid) {
        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.customerAccountUuid, Op.EQ, accountUuid);
        return q.find() != null;
    }

    private void handle(APIGetAccountUuidListByProxyMsg msg) {
        AccountVO accountVO = dbf.findByUuid(msg.getAccountUuid(), AccountVO.class);
        if (accountVO == null) {
            throw new IllegalArgumentException("input the correct accountUuid");
        }
        if (accountVO.getType() != AccountType.Proxy) {
            throw new IllegalArgumentException("the account must bu proxy");
        }
        APIGetAccountUuidListByProxyReply reply = new APIGetAccountUuidListByProxyReply();

        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.accountUuid, Op.EQ, accountVO.getUuid());
        q.select(ProxyAccountRefVO_.customerAccountUuid);
        List<String> customerAccountUuids = q.listValue();
        reply.setAccountUuids(customerAccountUuids);
        bus.reply(msg, reply);
    }

    private void handle(APIVerifyRepetitionMsg msg) {
        APIVerifyRepetitionReply reply = new APIVerifyRepetitionReply();

        if (msg.getAccountName() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.name, Op.EQ, msg.getAccountName());
            if (q.isExists()) {
                reply.setAccountName(true);
            }
        }
        if (msg.getAccountEmail() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.email, Op.EQ, msg.getAccountEmail());
            if (q.isExists()) {
                reply.setAccountEmail(true);
            }
        }
        if (msg.getAccountPhone() != null) {
            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.phone, Op.EQ, msg.getAccountPhone());
            if (q.isExists()) {
                reply.setAccountPhone(true);
            }
        }

        if (msg.getUserName() != null) {
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(UserVO_.name, Op.EQ, msg.getUserName());
            if (q.isExists()) {
                reply.setUserName(true);
            }
        }
        if (msg.getUserEmail() != null) {
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(UserVO_.email, Op.EQ, msg.getUserEmail());
            if (q.isExists()) {
                reply.setUserEmail(true);
            }
        }
        if (msg.getUserPhone() != null) {
            SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
            q.add(UserVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(UserVO_.phone, Op.EQ, msg.getUserPhone());
            if (q.isExists()) {
                reply.setUserPhone(true);
            }
        }

        if (msg.getRoleName() != null) {
            SimpleQuery<RoleVO> q = dbf.createQuery(RoleVO.class);
            q.add(RoleVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            q.add(RoleVO_.name, Op.EQ, msg.getRoleName());
            if (q.isExists()) {
                reply.setRoleName(true);
            }
        }

        bus.reply(msg, reply);
    }

    private void handle(APIAccountPWDBackByPhoneMsg msg) {
        APIAccountPWDBackByPhoneEvent evt = new APIAccountPWDBackByPhoneEvent(msg.getId());

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[%s, %s]", msg.getPhone(), msg.getCode()));
        } else {

            SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
            q.add(AccountVO_.phone, Op.EQ, msg.getPhone());
            AccountVO account = q.find();
            if (account.getType() == AccountType.SystemAdmin) {
                throw new ApiMessageInterceptionException(argerr("SystemAdmin禁止使用找回密码功能[phone %s]", msg.getPhone()));
            }
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
            } else {
                throw new ApiMessageInterceptionException(argerr("account[%s] is not exists", msg.getAccountName()));
            }
        } else {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[%s, %s]", msg.getPhone(), msg.getCode()));
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
        api.setSecretKey(getRandomString(30));
        api.setSecretId(getRandomString(16));
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
            if (session == null) {
                valid = false;
            } else if (current.after(session.getExpiredDate())) {
                valid = false;
                identiyInterceptor.logOutSession(session.getUuid());
            } else {
                s = session.toSessionInventory();
            }


        }

        if (valid) {
            reply.setAccountUuid(s.getAccountUuid());
            reply.setUserUuid(s.getUserUuid());
            reply.setType(s.getType());
        }

        reply.setValidSession(valid);

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
            AccountVO vo = dbf.findByUuid(s.getAccountUuid(), AccountVO.class);
            reply.setAccountName(vo.getName());
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

        if (!imageCodeService.ValidateImageCode(msg.getImageUuid(), msg.getImageCode())) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Incorrect Validate Image Code"));
            bus.reply(msg, reply);
            return;
        }

        AccountVO account;

        SimpleQuery<AccountVO> accountq = dbf.createQuery(AccountVO.class);
        if (msg.getAccountName() != null) {
            accountq.add(AccountVO_.name, Op.EQ, msg.getAccountName());
        } else if (msg.getAccountPhone() != null) {
            accountq.add(AccountVO_.phone, Op.EQ, msg.getAccountPhone());
        } else if (msg.getAccountEmail() != null) {
            accountq.add(AccountVO_.email, Op.EQ, msg.getAccountEmail());
        }
        account = accountq.find();
        if (account == null) {
            throw new OperationFailureException(argerr("account[%s] not found", msg.getAccountName()));
        }

        if (account.getStatus() == AccountStatus.Disabled) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "frozen account"
            ));
            bus.reply(msg, reply);
            return;
        }

        SimpleQuery<UserVO> q = dbf.createQuery(UserVO.class);
        q.add(UserVO_.accountUuid, Op.EQ, account.getUuid());
        q.add(UserVO_.password, Op.EQ, msg.getPassword());
        if (msg.getUserName() != null) {
            q.add(UserVO_.name, Op.EQ, msg.getUserName());
        } else if (msg.getUserPhone() != null) {
            q.add(UserVO_.phone, Op.EQ, msg.getUserPhone());
        } else if (msg.getUserEmail() != null) {
            q.add(UserVO_.email, Op.EQ, msg.getUserEmail());
        }
        UserVO user = q.find();

        if (user == null) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "wrong account or username or password "
            ));
            bus.reply(msg, reply);
            return;
        } else if (user.getStatus() == AccountStatus.Disabled) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "frozen user"
            ));
            bus.reply(msg, reply);
            return;
        }

        reply.setInventory(identiyInterceptor.initSession(account, user));
        loginLog(user.getClass().getSimpleName(), user.getName(), user.getUuid(), msg);
        bus.reply(msg, reply);
    }

    private void handle(APILogInByAccountMsg msg) {
        APILogInReply reply = new APILogInReply();

        if (!imageCodeService.ValidateImageCode(msg.getImageUuid(), msg.getImageCode())) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Incorrect Validate Image Code"));
            bus.reply(msg, reply);
            return;
        }

        if (msg.getAccountName() == null && msg.getEmail() == null &&
                msg.getPhone() == null) {
            throw new OperationFailureException(operr("account/email/phone all is null"));

        }
        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        if (msg.getAccountName() != null) {
            q.add(AccountVO_.name, Op.EQ, msg.getAccountName());
        } else if (msg.getEmail() != null) {
            q.add(AccountVO_.email, Op.EQ, msg.getEmail());
        } else if (msg.getPhone() != null) {
            q.add(AccountVO_.phone, Op.EQ, msg.getPhone());
        }

//        q.add(AccountVO_.password, Op.EQ, msg.getPassword());

        AccountVO vo = q.find();
        if (vo == null) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Incorrect login name"));
            bus.reply(msg, reply);
        } else if (vo.getStatus() == AccountStatus.Disabled) {
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Blocked account"));
            bus.reply(msg, reply);
        } else if (!msg.getPassword().equals(vo.getPassword())) {
            if (msg.getPlaintext() != null) {
                if (!check_password(msg.getPlaintext(), vo.getPassword())) {
                    reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                            "Incorrect password"));
                    bus.reply(msg, reply);
                } else {
                    vo.setPassword(DigestUtils.sha512Hex(msg.getPlaintext()));
                    dbf.updateAndRefresh(vo);
                    reply.setInventory(identiyInterceptor.initSession(vo, null));
                    loginLog(vo.getClass().getSimpleName(), vo.getName(), vo.getUuid(), msg);
                }
            } else {
                reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                        "Incorrect password"));
                bus.reply(msg, reply);
            }
        } else {
            reply.setInventory(identiyInterceptor.initSession(vo, null));
            loginLog(vo.getClass().getSimpleName(), vo.getName(), vo.getUuid(), msg);
        }

        bus.reply(msg, reply);
    }

    private boolean check_password(String password, String hash) {
        StringBuffer cmdStr = new StringBuffer();
        cmdStr.append("python -c ");
        cmdStr.append("'import passlib.hash;");
        cmdStr.append("print passlib.hash.sha512_crypt.verify(\"");
        cmdStr.append(password);
        cmdStr.append("\",");
        cmdStr.append("\"");
        cmdStr.append(hash);
        cmdStr.append("\")'");

        String cmd = cmdStr.toString();
        ShellResult sret = ShellUtils.runAndReturn(cmd);

        sret.raiseExceptionIfFail();
        if (sret.getStdout().trim().equals("True")) {
            return true;
        }

        return false;

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
                api.setSecretKey(getRandomString(30));
                api.setSecretId(getRandomString(16));
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
        } else if (msg instanceof APIDeleteProxyAccountRefMsg) {
            validate((APIDeleteProxyAccountRefMsg) msg);
        } else if (msg instanceof APIAccountMailAuthenticationMsg) {
            validate((APIAccountMailAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            validate((APIAccountPhoneAuthenticationMsg) msg);
        }

        setServiceId(msg);

        return msg;
    }

    private void validate(APIAccountPhoneAuthenticationMsg msg) {
        validateSystemAdminOnlySelf(msg.getAccountUuid(), msg);
    }

    private void validate(APIAccountMailAuthenticationMsg msg) {
        validateSystemAdminOnlySelf(msg.getAccountUuid(), msg);
    }

    private void validate(APIDeleteProxyAccountRefMsg msg) {

    }

    private void validate(APIRegisterAccountMsg msg) {
        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match"));
        }
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
        q.add(ProxyAccountRefVO_.customerAccountUuid, Op.EQ, msg.getUuid());

        if (msg.getSession().getType() == AccountType.SystemAdmin || q.isExists()) {
            validateSystemAdminOnlySelf(msg.getAccountUuid(), msg);
        } else {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot reset the password of the other account[uuid: %s]",
                    msg.getAccountUuid(), msg.getUuid()));
        }
    }

    private void validateSystemAdminOnlySelf(String accountUuid, APIMessage msg) {
        AccountVO account = dbf.findByUuid(accountUuid, AccountVO.class);
        if (account.getType() == AccountType.SystemAdmin) {
            if (!msg.getSession().isAdminAccountSession()) {
                throw new ApiMessageInterceptionException(argerr("禁止修改系统管理员信息"));
            }
        }
    }

    private void validate(APIResetAccountApiSecurityMsg msg) {

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getAccountUuid()));
        }

        validateSystemAdminOnlySelf(msg.getAccountUuid(), msg);
    }

    private void validate(APIGetAccountApiKeyMsg msg) {

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getAccountUuid()));
        }

    }

    private void validate(APILogInByUserMsg msg) {
        if (msg.getAccountName() == null && msg.getAccountEmail() == null &&
                msg.getAccountPhone() == null) {
            throw new ApiMessageInterceptionException(argerr(
                    "accountName and accountEmail and accountPhone cannot all be null, you must specify at least one"
            ));
        }

        if (msg.getUserEmail() == null && msg.getUserName() == null &&
                msg.getUserPhone() == null) {
            throw new ApiMessageInterceptionException(argerr(
                    "accountName and accountEmail and accountPhone cannot all be null, you must specify at least one"
            ));
        }
    }

    private void validate(APIUpdateUserPhoneMsg msg) {
        if (!msg.getOldphone().equals(dbf.findByUuid(msg.getSession().getUserUuid(),
                UserVO.class).getPhone())) {
            throw new ApiMessageInterceptionException(argerr("wrong oldphone"));
        }

        if (!smsService.validateVerificationCode(msg.getOldphone(), msg.getOldcode())
                || !smsService.validateVerificationCode(msg.getNewphone(), msg.getNewcode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateUserPWDMsg msg) {

        if (msg.getPhone() != null) {
            if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
                throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                        msg.getSession().getAccountUuid()));
            }
        } else if (msg.getEmail() != null) {
            if (!mailService.ValidateMailCode(msg.getEmail(), msg.getCode())) {
                throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                        msg.getSession().getAccountUuid()));
            }
        } else {
            throw new ApiMessageInterceptionException(argerr("email and phone all is null"));
        }

    }

    private void validate(APIUpdateUserEmailMsg msg) {

        if (!msg.getOldEmail().equals(dbf.findByUuid(msg.getSession().getUserUuid(),
                UserVO.class).getEmail())) {
            throw new ApiMessageInterceptionException(argerr("wrong oldmail"));
        }

        if (!mailService.ValidateMailCode(msg.getOldEmail(), msg.getOldCode()) ||
                !mailService.ValidateMailCode(msg.getNewEmail(), msg.getNewCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }
    }

    private void validate(APIUpdateAccountPWDMsg msg) {
        validateSystemAdminOnlySelf(msg.getAccountUuid(), msg);

        if (msg.getPhone() != null && !msg.getPhone().equals(dbf.findByUuid(msg.getAccountUuid(),
                AccountVO.class).getPhone())) {
            throw new ApiMessageInterceptionException(argerr("Wrong Old Phone"));
        }

        if (msg.getPhone() != null) {
            if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
                throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                        msg.getSession().getAccountUuid()));
            }
        } else if (msg.getEmail() != null) {
            if (!mailService.ValidateMailCode(msg.getEmail(), msg.getCode())) {
                throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                        msg.getSession().getAccountUuid()));
            }
        } else {
            throw new ApiMessageInterceptionException(argerr("email/phone all is null"));
        }

    }

    private void validate(APIUpdateAccountPhoneMsg msg) {
        if (!msg.getOldphone().equals(dbf.findByUuid(msg.getSession().getAccountUuid(),
                AccountVO.class).getPhone())) {
            throw new ApiMessageInterceptionException(argerr("wrong oldphone"));
        }

        if (!smsService.validateVerificationCode(msg.getOldphone(), msg.getOldcode()) ||
                !smsService.validateVerificationCode(msg.getNewphone(), msg.getNewcode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }

        validateSystemAdminOnlySelf(msg.getSession().getAccountUuid(), msg);
    }

    private void validate(APIUpdateAccountEmailMsg msg) {

        if (!msg.getOldEmail().equals(dbf.findByUuid(msg.getSession().getAccountUuid(),
                AccountVO.class).getEmail())) {
            throw new ApiMessageInterceptionException(argerr("wrong oldmail"));
        }

        validateSystemAdminOnlySelf(msg.getSession().getAccountUuid(), msg);

        if (!mailService.ValidateMailCode(msg.getOldEmail(), msg.getOldCode()) ||
                !mailService.ValidateMailCode(msg.getNewEmail(), msg.getNewCode())) {
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
                && msg.getSession().getType() != AccountType.Proxy) {
            throw new ApiMessageInterceptionException(argerr("unable to create a account. account[uuid:%s] is not admin or proxy",
                    msg.getSession().getAccountUuid()));
        }

        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        q.add(AccountVO_.name, Op.EQ, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create an account. account name %s already is exsists", msg.getName()));
        }

        SimpleQuery<AccountVO> q1 = dbf.createQuery(AccountVO.class);
        q1.add(AccountVO_.phone, Op.EQ, msg.getPhone());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create an account. account phone %s already is exsists", msg.getName()));
        }

        SimpleQuery<AccountVO> q2 = dbf.createQuery(AccountVO.class);
        q2.add(AccountVO_.email, Op.EQ, msg.getEmail());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create an account. account email %s already is exsists", msg.getName()));
        }


        if (msg.getName().equals("admin")) {
            throw new ApiMessageInterceptionException(argerr("can not use the name[admin]"));

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
        if (AccountType.SystemAdmin.toString().equalsIgnoreCase(msg.getType())) {
            throw new OperationFailureException(operr("cannot set account type to SystemAdmin"));
        }

        if (msg.getSession().getType() != AccountType.SystemAdmin &&
                !msg.getSession().getAccountUuid().equals(msg.getUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot update another account[uuid: %s]",
                    msg.getSession().getAccountUuid(), msg.getUuid()));
        }

        validateSystemAdminOnlySelf(msg.getUuid(), msg);
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

    @Override
    public List<Quota> reportQuota() {

        UserQuotaOperator quotaOperator = new UserQuotaOperator();
        // interface quota
        Quota quota = new Quota();
        quota.setOperator(quotaOperator);
        quota.addMessageNeedValidation(APICreateUserMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(AccountConstant.QUOTA_USER_NUM);
        p.setValue(QuotaConstant.QUOTA_USER_NUM);
        quota.addPair(p);


        return list(quota);
    }


    private void loginLog(String type, String name, String uuid, APIMessage msg) {

        LoginLogVO vo = dbf.findByUuid(uuid, LoginLogVO.class);

        if (vo == null) {
            vo = new LoginLogVO();
            vo.setUuid(uuid);
            vo.setName(name);
            vo.setType(type);
            vo.setLastLoginIp(msg.getIp());
            vo.setLastLoginTime(dbf.getCurrentSqlTime());
            vo.setCreateDate(dbf.getCurrentSqlTime());
            dbf.persistAndRefresh(vo);
        } else {
            vo.setLastLoginIp(msg.getIp());
            vo.setLastLoginTime(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(vo);
        }

    }
}
