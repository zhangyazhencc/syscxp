package com.syscxp.account.identity;

import com.syscxp.account.header.account.*;
import com.syscxp.account.header.identity.*;
import com.syscxp.account.header.user.*;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.identity.*;
import com.syscxp.sms.MailService;
import com.syscxp.sms.SmsService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.syscxp.core.Platform;
import com.syscxp.core.cascade.CascadeFacade;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.errorcode.ErrorFacade;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.ExceptionDSL;
import com.syscxp.utils.Utils;

import com.syscxp.utils.logging.CLogger;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class AccountBase extends AbstractAccount {
    private static final CLogger logger = Utils.getLogger(AccountBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private AccountManager acntMgr;
    @Autowired
    private CascadeFacade casf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;

    private AccountVO vo;

    @Autowired
    private IdentiyInterceptor identiyInterceptor;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MailService mailService;

    public AccountBase(AccountVO vo) {
        this.vo = vo;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateAccountMsg) {
            handle((APICreateAccountMsg) msg);
        } else if (msg instanceof APICreateUserMsg) {
            handle((APICreateUserMsg) msg);
        } else if (msg instanceof APIUpdateUserPWDMsg) {
            handle((APIUpdateUserPWDMsg) msg);
        } else if (msg instanceof APIUpdateAccountPWDMsg) {
            handle((APIUpdateAccountPWDMsg) msg);
        } else if (msg instanceof APIUpdateUserPhoneMsg) {
            handle((APIUpdateUserPhoneMsg) msg);
        } else if (msg instanceof APIUpdateAccountPhoneMsg) {
            handle((APIUpdateAccountPhoneMsg) msg);
        } else if (msg instanceof APIUpdateAccountEmailMsg) {
            handle((APIUpdateAccountEmailMsg) msg);
        } else if (msg instanceof APIUpdateUserEmailMsg) {
            handle((APIUpdateUserEmailMsg) msg);
        } else if (msg instanceof APIUpdateAccountMsg) {
            handle((APIUpdateAccountMsg) msg);
        } else if (msg instanceof APIUpdateUserMsg) {
            handle((APIUpdateUserMsg) msg);
        } else if (msg instanceof APICreateRoleMsg) {
            handle((APICreateRoleMsg) msg);
        } else if (msg instanceof APIDetachPolicyFromUserMsg) {
            handle((APIDetachPolicyFromUserMsg) msg);
        } else if (msg instanceof APIDeleteRoleMsg) {
            handle((APIDeleteRoleMsg) msg);
        } else if (msg instanceof APIAttachPolicyToUserMsg) {
            handle((APIAttachPolicyToUserMsg) msg);
        } else if (msg instanceof APIUpdatePolicyMsg) {
            handle((APIUpdatePolicyMsg) msg);
        } else if (msg instanceof APICreatePolicyMsg) {
            handle((APICreatePolicyMsg) msg);
        } else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        } else if (msg instanceof APIAccountPhoneAuthenticationMsg) {
            handle((APIAccountPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIUserPhoneAuthenticationMsg) {
            handle((APIUserPhoneAuthenticationMsg) msg);
        } else if (msg instanceof APIUpdateAccountContactsMsg) {
            handle((APIUpdateAccountContactsMsg) msg);
        } else if (msg instanceof APICreateAccountContactsMsg) {
            handle((APICreateAccountContactsMsg) msg);
        } else if (msg instanceof APIDeleteAccountContactsMsg) {
            handle((APIDeleteAccountContactsMsg) msg);
        } else if (msg instanceof APIResetAccountPWDMsg) {
            handle((APIResetAccountPWDMsg) msg);
        } else if (msg instanceof APIResetAccountApiSecurityMsg) {
            handle((APIResetAccountApiSecurityMsg) msg);
        } else if (msg instanceof APIUpdateApiAllowIPMsg) {
            handle((APIUpdateApiAllowIPMsg) msg);
        } else if (msg instanceof APIGetAccountApiKeyMsg) {
            handle((APIGetAccountApiKeyMsg) msg);
        } else if (msg instanceof APIGetAccountMsg) {
            handle((APIGetAccountMsg) msg);
        } else if (msg instanceof APIGetUserMsg) {
            handle((APIGetUserMsg) msg);
        } else if (msg instanceof APIDeleteUserMsg) {
            handle((APIDeleteUserMsg) msg);
        } else if (msg instanceof APIResetUserPWDMsg) {
            handle((APIResetUserPWDMsg) msg);
        } else if (msg instanceof APIUpdateRoleMsg) {
            handle((APIUpdateRoleMsg) msg);
        } else if (msg instanceof APIAccountMailAuthenticationMsg) {
            handle((APIAccountMailAuthenticationMsg) msg);
        } else if (msg instanceof APIDeleteProxyAccountRefMsg) {
            handle((APIDeleteProxyAccountRefMsg) msg);
        } else if (msg instanceof APIUserMailAuthenticationMsg) {
            handle((APIUserMailAuthenticationMsg)msg);
        } else if (msg instanceof APIGetSecretKeyMsg) {
            handle((APIGetSecretKeyMsg)msg);
        } else if (msg instanceof APILogInBySecretIdMsg) {
            handle((APILogInBySecretIdMsg)msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }


    }

    private void handle(APILogInBySecretIdMsg msg) {
        APILogInReply reply = new APILogInReply();

        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.secretId, SimpleQuery.Op.EQ, msg.getSecretId());
        q.add(AccountApiSecurityVO_.secretKey, SimpleQuery.Op.EQ, msg.getSecretKey());

        if(q.isExists()){
            AccountApiSecurityVO api = q.find();
            reply.setInventory(identiyInterceptor.initSession(
                    dbf.findByUuid(api.getAccountUuid(),AccountVO.class), null));

        }else{
            reply.setError(errf.instantiateErrorCode(IdentityErrors.AUTHENTICATION_ERROR,
                    "Incorrect secretId or secretKey"));
        }

        bus.reply(msg, reply);
    }

    private void handle(APIGetSecretKeyMsg msg) {

        APIGetSecretKeyReply reply = new APIGetSecretKeyReply();

        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.secretId, SimpleQuery.Op.EQ, msg.getSecretId());
        AccountApiSecurityVO api = q.find();
        reply.setSecretKey(api.getSecretKey());
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIUpdateRoleMsg msg) {

        RoleVO role = dbf.findByUuid(msg.getUuid(), RoleVO.class);

        if (msg.getName() != null) {
            role.setName(msg.getName());
        }

        if (msg.getDescription() != null) {
            role.setDescription(msg.getDescription());
        }

        if (msg.getPolicyUuids() != null) {

            List<RolePolicyRefVO> list = new ArrayList();
            RolePolicyRefVO vo = null;

            UpdateQuery.New(RolePolicyRefVO.class).condAnd(RolePolicyRefVO_.roleUuid,
                    SimpleQuery.Op.EQ,msg.getUuid()).delete();

            for (String id : msg.getPolicyUuids()) {
                vo = new RolePolicyRefVO();
                vo.setRoleUuid(msg.getUuid());
                vo.setPolicyUuid(id);
                list.add(vo);
            }

            dbf.persistCollection(list);
        }


        role = dbf.getEntityManager().merge(role);

        APIUpdateRoleEvent evt = new APIUpdateRoleEvent(msg.getId());

        evt.setInventory(RoleInventory.valueOf(role));
        bus.publish(evt);
    }

    private void handle(APIResetAccountPWDMsg msg) {

        APIResetAccountPWDEvent evt = new APIResetAccountPWDEvent(msg.getId());
        AccountVO cont = dbf.findByUuid(msg.getUuid(), AccountVO.class);

        String pwd = getRandomString(12);

        cont.setPassword(DigestUtils.sha512Hex(pwd));
        dbf.updateAndRefresh(cont);

        evt.setPassword(pwd);
        bus.publish(evt);
    }

    private void handle(APIResetUserPWDMsg msg) {

        APIResetUserPWDEvent evt = new APIResetUserPWDEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);
        if (msg.getSession().getType() != AccountType.SystemAdmin &&
                !msg.getAccountUuid().equals(user.getAccountUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, it cannot reset the password of other user [uuid: %s]",
                    msg.getAccountUuid(), msg.getUuid()));
        }

        String pwd = getRandomString(12);

        user.setPassword(DigestUtils.sha512Hex(pwd));
        dbf.updateAndRefresh(user);

        evt.setPassword(pwd);
        bus.publish(evt);
    }

    private void handle(APIUpdateApiAllowIPMsg msg) {

        APIUpdateApiAllowIPEvent evt = new APIUpdateApiAllowIPEvent(msg.getId());
        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        AccountApiSecurityVO api = q.find();
        api.setAllowIp(msg.getAllowIP());

        evt.setInventory(AccountApiSecurityInventory.valueOf(dbf.updateAndRefresh(api)));
        bus.publish(evt);
    }

    private void handle(APIGetAccountApiKeyMsg msg) {

        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        AccountApiSecurityVO api = q.find();

        APIGetAccountApiKeyReply reply = new APIGetAccountApiKeyReply();
        reply.setInventory(AccountApiSecurityInventory.valueOf(api));
        bus.reply(msg, reply);
    }

    private void handle(APIGetAccountMsg msg) {
        APIGetAccountReply reply = new APIGetAccountReply();
        AccountVO account = dbf.findByUuid(msg.getAccountUuid(), AccountVO.class);

        reply.setInventory(AccountInventory.valueOf(account));
        bus.reply(msg, reply);
    }

    private void handle(APIGetUserMsg msg) {
        APIGetUserReply reply = new APIGetUserReply();

        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        reply.setInventory(UserInventory.valueOf(user));
        bus.reply(msg, reply);
    }

    private void handle(APIResetAccountApiSecurityMsg msg) {

        APIResetAccountApiSecurityEvent evt = new APIResetAccountApiSecurityEvent(msg.getId());

        SimpleQuery<AccountApiSecurityVO> q = dbf.createQuery(AccountApiSecurityVO.class);
        q.add(AccountApiSecurityVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        AccountApiSecurityVO api = q.find();

        api.setSecretId(getRandomString(16));
        api.setSecretKey(getRandomString(30));

        dbf.updateAndRefresh(api);

        evt.setInventory(AccountApiSecurityInventory.valueOf(api));
        bus.publish(evt);
    }


    private void handle(APICreateAccountContactsMsg msg) {

        AccountContactsVO acvo = new AccountContactsVO();
        acvo.setUuid(Platform.getUuid());
        acvo.setAccountUuid(msg.getAccountUuid());
        acvo.setDescription(msg.getDescription());
        acvo.setName(msg.getName());
        acvo.setPhone(msg.getPhone());
        acvo.setEmail(msg.getEmail());
        acvo.setNoticeWay(msg.getNoticeWay());

        APICreateAccountContactsEvent evt = new APICreateAccountContactsEvent(msg.getId());

        if((msg.getNoticeWay() == NoticeWay.phone && msg.getPhone() == null)
            ||(msg.getNoticeWay() == NoticeWay.email && msg.getEmail() == null)
                ||(msg.getNoticeWay() == NoticeWay.all && (msg.getEmail() == null || msg.getPhone() == null))){
            evt.setError(errf.stringToOperationError("Information mismatch"));
        }

        evt.setInventory(AccountContactsInventory.valueOf(dbf.persistAndRefresh(acvo)));
        bus.publish(evt);
    }


    private void handle(APIDeleteAccountContactsMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), AccountContactsVO.class);

        APIDeleteAccountContactsEvent evt = new APIDeleteAccountContactsEvent(msg.getId());

        bus.publish(evt);
    }

    private void handle(APIUpdateAccountContactsMsg msg) {
        APIUpdateAccountContactsEvent evt = new APIUpdateAccountContactsEvent(msg.getId());
        AccountContactsVO cont = dbf.findByUuid(msg.getUuid(), AccountContactsVO.class);

        if (msg.getName() != null) {
            cont.setName(msg.getName());
        }
        if (msg.getDescription() != null) {
            cont.setDescription(msg.getDescription());
        }
        if (msg.getEmail() != null) {
            cont.setEmail(msg.getEmail());
        }
        if (msg.getPhone() != null) {
            cont.setPhone(msg.getPhone());
        }
        if (msg.getNoticeWay() != null) {
            cont.setNoticeWay(msg.getNoticeWay());
        }

        evt.setInventory(AccountContactsInventory.valueOf(dbf.updateAndRefresh(cont)));

        bus.publish(evt);
    }

    private void handle(APIAccountPhoneAuthenticationMsg msg) {

        APIAccountPhoneAuthenticationEvent evt = new APIAccountPhoneAuthenticationEvent(msg.getId());
        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);

        if (!smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        } else {
            if (account.getPhoneStatus() == ValidateStatus.Unvalidated) {
                account.setPhone(msg.getPhone());
                account.setPhoneStatus(ValidateStatus.Validated);
                dbf.updateAndRefresh(account);
            }
        }

        evt.setPhone(account.getPhone());

        bus.publish(evt);
    }

    private void handle(APIAccountMailAuthenticationMsg msg) {

        APIAccountMailAuthenticationEvent evt = new APIAccountMailAuthenticationEvent(msg.getId());
        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);

        if (!mailService.ValidateMailCode(msg.getMail(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }else{
            if (account.getEmailStatus() == ValidateStatus.Unvalidated) {
                account.setEmail(msg.getMail());
                account.setEmailStatus(ValidateStatus.Validated);
                dbf.updateAndRefresh(account);
            }
        }

        evt.setMail(account.getEmail());

        bus.publish(evt);
    }

    private void handle(APIUserMailAuthenticationMsg msg) {

        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);

        if (!mailService.ValidateMailCode(msg.getMail(), msg.getCode())) {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }else{
            if (user.getEmailStatus() == ValidateStatus.Unvalidated) {
                user.setEmailStatus(ValidateStatus.Validated);
                user.setEmail(msg.getMail());
                dbf.updateAndRefresh(user);
            }
        }

        APIUserMailAuthenticationEvent evt = new APIUserMailAuthenticationEvent(msg.getId());
        evt.setMail(user.getEmail());

        bus.publish(evt);
    }

    private void handle(APIUserPhoneAuthenticationMsg msg) {

        APIUserPhoneAuthenticationEvent evt = new APIUserPhoneAuthenticationEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);

//        if (user.getPhoneStatus() == ValidateStatus.Unvalidated &&
        if (user.getPhoneStatus().toString().equals(ValidateStatus.Unvalidated.toString()) &&
                smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            user.setPhone(msg.getPhone());
            user.setPhoneStatus(ValidateStatus.Validated);
            dbf.updateAndRefresh(user);
        } else {
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }

        evt.setPhone(msg.getPhone());
        bus.publish(evt);
    }

    private void handle(APIUpdateUserPWDMsg msg) {

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        if ((msg.getPhone() != null && user.getPhone().equals(msg.getPhone()))
            ||(msg.getEmail() != null && user.getEmail().equals(msg.getEmail()))) {

            if (user.getPassword().equals(msg.getOldpassword())) {
                user.setPassword(msg.getNewpassword());
                evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
            } else {
                throw new CloudRuntimeException("bad old passwords");
            }
        } else{
            throw new ApiMessageInterceptionException(argerr("wrong phone"));

        }

        bus.publish(evt);
    }

    private void handle(APIUpdateAccountPWDMsg msg) {
        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());

        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);
        if (account.getPassword().equals(msg.getOldpassword())) {
            account.setPassword(msg.getNewpassword());
            evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));
        } else {
            throw new CloudRuntimeException("bad old passwords");
        }

        bus.publish(evt);
    }

    private void handle(APIUpdateAccountPhoneMsg msg) {
        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());

        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);
        account.setPhone(msg.getNewphone());
        evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));

        bus.publish(evt);
    }

    private void handle(APIUpdateUserPhoneMsg msg) {
        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());

        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        user.setPhone(msg.getNewphone());
        evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));

        bus.publish(evt);
    }

    private void handle(APIUpdateAccountEmailMsg msg) {

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());

        AccountVO account = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);
        account.setEmail(msg.getNewEmail());
        evt.setInventory(AccountInventory.valueOf(dbf.updateAndRefresh(account)));

        bus.publish(evt);
    }


    private void handle(APIUpdateUserEmailMsg msg) {
        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());

        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        user.setEmail(msg.getNewEmail());
        evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));

        bus.publish(evt);
    }

    @Transactional
    private void handle(APIUpdateAccountMsg msg) {

        AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());

        boolean update = false;
        if (msg.getCompany() != null) {
            account.setCompany(msg.getCompany());
            update = true;
        }
        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
            update = true;
        }
        if (msg.getEmail() != null && !msg.getEmail().equalsIgnoreCase(account.getEmail())) {
            account.setEmail(msg.getEmail());
            account.setEmailStatus(ValidateStatus.Unvalidated);
            update = true;
        }

        if (msg.getPhone() != null && !msg.getPhone().equalsIgnoreCase(account.getPhone())) {
            account.setPhone(msg.getPhone());
            account.setPhoneStatus(ValidateStatus.Unvalidated);
            update = true;
        }

        if (msg.getTrueName() != null) {
            account.setTrueName(msg.getTrueName());
            update = true;
        }

        if (msg.getIndustry() != null) {
            account.setIndustry(msg.getIndustry());
            update = true;
        }

//        if (msg.getSession().getType() == AccountType.SystemAdmin) {
        if (msg.getSession().getType().toString().equals(AccountType.SystemAdmin.toString())) {

            if (msg.getStatus() != null) {
                account.setStatus(msg.getStatus());
                if(msg.getStatus() == AccountStatus.Disabled){
                    List<SessionVO> list = dbf.createQuery(SessionVO.class).add(SessionVO_.accountUuid,
                            SimpleQuery.Op.EQ,msg.getUuid()).list();
                    for(SessionVO vo: list){
                        identiyInterceptor.getSessions().remove(vo.getUuid());
                    }
                    UpdateQuery.New(SessionVO.class).condAnd(SessionVO_.accountUuid,
                            SimpleQuery.Op.EQ,msg.getUuid()).delete();
                }
            }

            if (msg.getType() != null) {

                if(msg.getType().equals(AccountType.Proxy.toString()) &&
                        dbf.createQuery(ProxyAccountRefVO.class).add(ProxyAccountRefVO_.customerAccountUuid,
                                SimpleQuery.Op.EQ,msg.getUuid()).isExists()){
                    evt.setError(Platform.argerr("先解绑此账户[%s]",msg.getUuid()));
                }else{
                    account.setType(AccountType.valueOf(msg.getType()));
                }

                if(msg.getType().equals(AccountType.Normal.toString()) &&
                        dbf.createQuery(ProxyAccountRefVO.class).add(ProxyAccountRefVO_.accountUuid,
                                SimpleQuery.Op.EQ,msg.getUuid()).isExists()){
                    dbf.removeCollection(dbf.createQuery(ProxyAccountRefVO.class).add(ProxyAccountRefVO_.accountUuid,
                            SimpleQuery.Op.EQ,msg.getUuid()).list(),ProxyAccountRefVO.class);
                    account.setType(AccountType.valueOf(msg.getType()));
                }



            }

            if (msg.getGrade() != null) {
                account.getAccountExtraInfo().setGrade(msg.getGrade());
            }
            account.getAccountExtraInfo().setUserUuid(msg.getUserUuid());
            update = true;

        }

        if (update) {
            account = dbf.getEntityManager().merge(account);
        }

        evt.setInventory(AccountInventory.valueOf(account));
        bus.publish(evt);

    }

    @Transactional
    private void handle(APIUpdateUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);

        if (msg.getName() != null) {
            user.setName(msg.getName());
        }
        if (msg.getDescription() != null) {
            user.setDescription(msg.getDescription());
        }

        if (msg.getDepartment() != null) {
            user.setDepartment(msg.getDepartment());
        }
        if (msg.getEmail() != null && !msg.getEmail().equalsIgnoreCase(user.getEmail())) {
            user.setEmail(msg.getEmail());
            user.setEmailStatus(ValidateStatus.Unvalidated);
        }
        if (msg.getPhone() != null && !msg.getPhone().equalsIgnoreCase(user.getPhone())) {
            user.setPhone(msg.getPhone());
            user.setPhoneStatus(ValidateStatus.Unvalidated);
        }
        if (msg.getStatus() != null) {
            user.setStatus(msg.getStatus());
        }
        if (msg.getTrueName() != null) {
            user.setTrueName(msg.getTrueName());
        }

        if (msg.getSession().getType() == AccountType.SystemAdmin && msg.getUserType() != null) {
            user.setUserType(msg.getUserType());
        }

        if (msg.getRoleUuid() != null){
            RoleVO role = dbf.findByUuid(msg.getRoleUuid(), RoleVO.class);
            Set<RoleVO> roleSet = new HashSet<>();
            if (role != null) {
                roleSet.add(role);
            }
            user.setRoleSet(roleSet);
        }

        user = dbf.getEntityManager().merge(user);

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(user));
        bus.publish(evt);

    }

    private void handle(APIDeleteUserMsg msg) {
        APIDeleteUserEvent evt = new APIDeleteUserEvent(msg.getId());

        dbf.removeByPrimaryKey(msg.getUuid(), UserVO.class);
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateAccountMsg msg) {

        AccountVO accountVO = new AccountVO();

        accountVO.setUuid(Platform.getUuid());
        accountVO.setName(msg.getName());
        accountVO.setPassword(msg.getPassword());
        accountVO.setCompany(msg.getCompany());
        accountVO.setDescription(msg.getDescription());
        accountVO.setEmail(msg.getEmail());
        accountVO.setIndustry(msg.getIndustry());
        accountVO.setPhone(msg.getPhone());
        accountVO.setTrueName(msg.getTrueName());
        accountVO.setStatus(AccountStatus.Available);
        if (msg.getType() != null && (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession())) {
            accountVO.setType(AccountType.valueOf(msg.getType()));
        } else {
            accountVO.setType(AccountType.Normal);
        }

        accountVO.setPhoneStatus(ValidateStatus.Unvalidated);
        accountVO.setEmailStatus(ValidateStatus.Unvalidated);

        AccountExtraInfoVO ext = new AccountExtraInfoVO();
        ext.setUuid(accountVO.getUuid());
        ext.setCreateWay(msg.getSession().getType().toString());
        if (msg.getGrade() != null) {
            ext.setGrade(msg.getGrade());
        } else {
            ext.setGrade(AccountGrade.Normal);
        }

        ext.setUserUuid(msg.getUserUuid());

        accountVO.setAccountExtraInfo(ext);

        dbf.getEntityManager().persist(accountVO);

        if (msg.getSession().isProxySession()) {
            ProxyAccountRefVO prevo = new ProxyAccountRefVO();
            prevo.setAccountUuid(msg.getAccountUuid());
            prevo.setCustomerAccountUuid(accountVO.getUuid());

            dbf.getEntityManager().persist(prevo);
            dbf.getEntityManager().refresh(prevo);
        }

        AccountApiSecurityVO api = new AccountApiSecurityVO();
        api.setUuid(Platform.getUuid());
        api.setAccountUuid(accountVO.getUuid());
        api.setSecretKey(getRandomString(30));
        api.setSecretId(getRandomString(16));
        dbf.getEntityManager().persist(api);


        dbf.getEntityManager().flush();
        dbf.getEntityManager().refresh(accountVO);
        dbf.getEntityManager().refresh(api);

        APICreateAccountEvent evt = new APICreateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(accountVO));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateUserMsg msg) {

        UserVO uservo = new UserVO();

        uservo.setUuid(Platform.getUuid());
        uservo.setAccountUuid(msg.getAccountUuid());
        uservo.setEmail(msg.getEmail());
        uservo.setName(msg.getName());
        uservo.setPassword(msg.getPassword());
        uservo.setPhone(msg.getPhone());
        uservo.setStatus(AccountStatus.Available);
        uservo.setTrueName(msg.getTrueName());
        uservo.setDepartment(msg.getDepartment());
        uservo.setDescription(msg.getDescription());

        uservo.setEmailStatus(ValidateStatus.Unvalidated);
        uservo.setPhoneStatus(ValidateStatus.Unvalidated);

        if (msg.getSession().getType() == AccountType.SystemAdmin && msg.getUserType() != null) {
            uservo.setUserType(msg.getUserType());
        } else {
            uservo.setUserType(UserType.normal);
        }

        if (msg.getRoleUuid() != null) {
            Set<RoleVO> roleSet = new HashSet<>();
            RoleVO role = dbf.findByUuid(msg.getRoleUuid(), RoleVO.class);
            if (role != null) {
                roleSet.add(role);
            }
            uservo.setRoleSet(roleSet);
        }

        uservo = dbf.persistAndRefresh(uservo);

        APICreateUserEvent evt = new APICreateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(uservo));
        bus.publish(evt);
    }


    @Transactional
    private void handle(APICreateRoleMsg msg) {

        RoleVO role = new RoleVO();
        role.setUuid(Platform.getUuid());
        role.setName(msg.getName());
        role.setAccountUuid(msg.getAccountUuid());
        role.setDescription(msg.getDescription());

        role = dbf.persistAndRefresh(role);

        RolePolicyRefVO vo = null;
        for (String id : msg.getPolicyUuids()) {
            vo = new RolePolicyRefVO();
            vo.setPolicyUuid(id);
            vo.setRoleUuid(role.getUuid());
            dbf.persistAndRefresh(vo);
        }

        APICreateRoleEvent evt = new APICreateRoleEvent(msg.getId());

        evt.setInventory(RoleInventory.valueOf(role));
        bus.publish(evt);
    }

    private void handle(APIDetachPolicyFromUserMsg msg) {
        SimpleQuery<UserRoleRefVO> q = dbf.createQuery(UserRoleRefVO.class);
        q.add(UserRoleRefVO_.roleUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        q.add(UserRoleRefVO_.userUuid, SimpleQuery.Op.EQ, msg.getUserUuid());
        UserRoleRefVO ref = q.find();
        if (ref != null) {
            dbf.remove(ref);
        } else {
            throw new OperationFailureException(operr("noting to be found"));
        }

        APIDetachPolicyFromUserEvent evt = new APIDetachPolicyFromUserEvent(msg.getId());

        bus.publish(evt);
    }

    @Transactional
    private void handle(APIDeleteRoleMsg msg) {
        UpdateQuery.New(RolePolicyRefVO.class).condAnd(RolePolicyRefVO_.roleUuid,
                SimpleQuery.Op.EQ,msg.getUuid()).delete();


        dbf.removeByPrimaryKey(msg.getUuid(), RoleVO.class);

        APIDeleteRoleEvent evt = new APIDeleteRoleEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIAttachPolicyToUserMsg msg) {
        APIAttachPolicyToUserEvent evt = new APIAttachPolicyToUserEvent(msg.getId());

        UserRoleRefVO upvo = new UserRoleRefVO();
        upvo.setRoleUuid(msg.getRoleUuid());
        upvo.setUserUuid(msg.getUserUuid());

        try {
            evt.setUpv(dbf.persistAndRefresh(upvo));
        } catch (Throwable t) {
            if (!ExceptionDSL.isCausedBy(t, ConstraintViolationException.class)) {
                throw t;
            }
        }

        bus.publish(evt);
    }

    private void handle(APICreatePolicyMsg msg) {

        PolicyVO auth = new PolicyVO();
        auth.setUuid(Platform.getUuid());
        auth.setPermission(msg.getPermission());
        auth.setName(msg.getName());
        auth.setSortId(msg.getSortId());
        auth.setType(msg.getType());
        auth.setAccountType(msg.getAccountType());

        APICreatePolicyEvent evt = new APICreatePolicyEvent(msg.getId());

        evt.setInventory(PolicyInventory.valueOf(dbf.persistAndRefresh(auth)));
        bus.publish(evt);
    }

    private void handle(APIUpdatePolicyMsg msg) {

        PolicyVO auth = dbf.findByUuid(msg.getUuid(), PolicyVO.class);

        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            auth.setName(msg.getName());
            update = true;
        }
        if (msg.getAccountType() != null) {
            auth.setAccountType(msg.getAccountType());
            update = true;
        }
        if (msg.getPermission() != null) {
            auth.setPermission(msg.getPermission());
            update = true;
        }
        if (msg.getType() != null) {
            auth.setType(msg.getType());
            update = true;
        }
        if (msg.getSortId() != null) {
            auth.setSortId(msg.getSortId());
            update = true;
        }

        if (update) {
            auth = dbf.updateAndRefresh(auth);
        }

        APIUpdatePolicyEvent evt = new APIUpdatePolicyEvent(msg.getId());

        evt.setInventory(PolicyInventory.valueOf(auth));
        bus.publish(evt);

    }

    private void handle(APIDeletePolicyMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), PolicyVO.class);
        APICreatePolicyEvent evt = new APICreatePolicyEvent(msg.getId());

        bus.publish(evt);
    }

    private void handle(APIDeleteProxyAccountRefMsg msg) {
        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.add(ProxyAccountRefVO_.customerAccountUuid, SimpleQuery.Op.EQ, msg.getUuid());

        if (q.isExists()) {
            ProxyAccountRefVO vo  =  q.find();
            dbf.removeByPrimaryKey(vo.getId(), ProxyAccountRefVO.class);
        }else{
            throw new ApiMessageInterceptionException(argerr("customerAcccount[uuid:%s] is not belong to this account[uuid:%s]"
                    ,msg.getUuid(),msg.getAccountUuid()));
        }
        APIDeleteProxyAccountRefEvent evt = new APIDeleteProxyAccountRefEvent(msg.getId());

        bus.publish(evt);
    }

    public String getRandomString(int length) {
        String base = "ABCDEFGFHJKMOPQRSTUVWXYZabcdefghjkmnopqrstuvwxy023456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
