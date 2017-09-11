package org.zstack.account.identity;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zstack.account.header.identity.*;
import org.zstack.core.Platform;
import org.zstack.core.cascade.CascadeFacade;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.*;
import org.zstack.core.errorcode.ErrorFacade;

import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.*;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.sms.SmsService;
import org.zstack.utils.ExceptionDSL;
import org.zstack.utils.Utils;

import org.zstack.utils.logging.CLogger;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
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
    private SmsService smsService;

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
        } else if (msg instanceof APICreatePolicyMsg) {
            handle((APICreatePolicyMsg) msg);
        } else if (msg instanceof APIDetachPolicyFromUserMsg) {
            handle((APIDetachPolicyFromUserMsg) msg);
        } else if (msg instanceof APIDeletePolicyMsg) {
            handle((APIDeletePolicyMsg) msg);
        } else if (msg instanceof APIAttachPolicyToUserMsg) {
            handle((APIAttachPolicyToUserMsg) msg);
        } else if (msg instanceof APIUpdatePermissionMsg) {
            handle((APIUpdatePermissionMsg) msg);
        } else if (msg instanceof APICreatePermissionMsg) {
            handle((APICreatePermissionMsg) msg);
        } else if (msg instanceof APIDeletePermissionMsg) {
            handle((APIDeletePermissionMsg) msg);
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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    /*
    private void handle(APIMailCodeSendMsg msg) {

        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        // 设定mail server
        senderImpl.setHost(" smtp.163.com ");

        // 建立邮件消息
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // 设置收件人，寄件人 用数组发送多个邮件
        // String[] array = new String[] {"sun111@163.com","sun222@sohu.com"};
        // mailMessage.setTo(array);
        mailMessage.setTo(" toEmail@sina.com ");
        mailMessage.setFrom(" userName@163.com ");
        mailMessage.setSubject(" 测试简单文本邮件发送！ ");
        mailMessage.setText(" 测试我的简单邮件发送机制！！ ");

        senderImpl.setUsername(" userName "); // 根据自己的情况,设置username
        senderImpl.setPassword(" password "); // 根据自己的情况, 设置password

        Properties prop = new Properties();
        prop.put(" mail.smtp.auth ", " true "); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put(" mail.smtp.timeout ", " 25000 ");
        senderImpl.setJavaMailProperties(prop);
        // 发送邮件
        senderImpl.send(mailMessage);

        System.out.println(" 邮件发送成功.. ");
    }
*/
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
        AccountVO account = dbf.findByUuid(msg.getAccountUuid(),AccountVO.class);

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

        api.setPublicKey(getRandomString(36));
        api.setPrivateKey(getRandomString(36));

        dbf.updateAndRefresh(api);

        evt.setInventory(AccountApiSecurityInventory.valueOf(api));
        bus.publish(evt);
    }


    private void handle(APICreateAccountContactsMsg msg) {

        AccountContactsVO acvo = new AccountContactsVO();
        acvo.setUuid(Platform.getUuid());
        acvo.setAccountUuid(msg.getAccountUuid());
        acvo.setName(msg.getName());
        acvo.setPhone(msg.getPhone());
        acvo.setEmail(msg.getEmail());
        acvo.setNoticeWay(msg.getNoticeWay());

        APICreateAccountContactsEvent evt = new APICreateAccountContactsEvent(msg.getId());
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

        if(msg.getName() != null){
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
        }else{
            if (account.getPhoneStatus() == ValidateStatus.Unvalidated) {
                account.setPhone(msg.getPhone());
                account.setPhoneStatus(ValidateStatus.Validated);
                dbf.updateAndRefresh(account);
            }
        }

        evt.setPhone(account.getPhone());

        bus.publish(evt);
    }

    private void handle(APIUserPhoneAuthenticationMsg msg) {

        APIUserPhoneAuthenticationEvent evt = new APIUserPhoneAuthenticationEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);

        if (user.getPhoneStatus() == ValidateStatus.Unvalidated && smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {
            user.setPhone(msg.getPhone());
            user.setPhoneStatus(ValidateStatus.Validated);
            dbf.updateAndRefresh(user);
        }else{
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }

        evt.setPhone(msg.getPhone());
        bus.publish(evt);
    }

    private void handle(APIUpdateUserPWDMsg msg) {

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        if (user.getPhone().equals(msg.getPhone())) {
            if (user.getPassword().equals(msg.getOldpassword())) {
                user.setPassword(msg.getNewpassword());
                evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
            } else {
                throw new CloudRuntimeException("bad old passwords");
            }
        }else{
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
        account.setEmail(msg.getEmail());
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

        boolean update = false;
        if (msg.getCompany() != null) {
            account.setCompany(msg.getCompany());
            update = true;
        }
        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
            update = true;
        }
        if (msg.getEmail() != null && ! msg.getEmail().equalsIgnoreCase(account.getEmail())) {
            account.setEmail(msg.getEmail());
            account.setEmailStatus(ValidateStatus.Unvalidated);
            update = true;
        }

        if (msg.getPhone() != null && ! msg.getPhone().equalsIgnoreCase(account.getPhone())) {
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

        if (msg.getSession().getType() == AccountType.SystemAdmin) {
            if (msg.getStatus() != null) {
                account.setStatus(msg.getStatus());
                update = true;
            }

            if (msg.getType() != null) {
                account.setType(AccountType.valueOf(msg.getType()));
                update = true;
            }

            if (msg.getGrade() != null) {
                account.getAccountExtraInfo().setGrade(msg.getGrade());
                update = true;
            }
        }

        if (update) {
            account = dbf.updateAndRefresh(account);
        }

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(account));
        bus.publish(evt);

    }

    @Transactional
    private void handle(APIUpdateUserMsg msg) {
        UserVO user = dbf.findByUuid(msg.getUuid(), UserVO.class);

        boolean update = false;

        if (msg.getName() != null) {
            user.setName(msg.getName());
            update = true;
        }
        if (msg.getDescription() != null) {
            user.setDescription(msg.getDescription());
            update = true;
        }

        if (msg.getDepartment() != null) {
            user.setDepartment(msg.getDepartment());
            update = true;
        }
        if (msg.getEmail() != null && ! msg.getEmail().equalsIgnoreCase(user.getEmail())) {
            user.setEmail(msg.getEmail());
            user.setEmailStatus(ValidateStatus.Unvalidated);
            update = true;
        }
        if (msg.getPhone() != null && ! msg.getPhone().equalsIgnoreCase(user.getPhone())) {
            user.setPhone(msg.getPhone());
            user.setPhoneStatus(ValidateStatus.Unvalidated);
            update = true;
        }
        if (msg.getStatus() != null) {
            user.setStatus(msg.getStatus());
            update = true;
        }
        if (msg.getTrueName() != null) {
            user.setTrueName(msg.getTrueName());
            update = true;
        }

        if(msg.getSession().getType() == AccountType.SystemAdmin && msg.getUserType() != null){
            user.setUserType(msg.getUserType());
            update = true;
        }

        if (msg.getPolicyUuid() != null) {
            Set<PolicyVO> policySet = new HashSet<>();
            PolicyVO policy = dbf.findByUuid(msg.getPolicyUuid(), PolicyVO.class);
            if (policy != null) {
                policySet.add(policy);
            }
            user.setPolicySet(policySet);

//            uprvo = new UserPolicyRefVO();
//            uprvo.setPolicyUuid(msg.getPolicyUuid());
//            uprvo.setUserUuid(msg.getUuid());
//            dbf.persist(uprvo);

            update = true;
        }

        if (update) {
            user = dbf.updateAndRefresh(user);
        }

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
        if(msg.getType() != null){
            accountVO.setType(AccountType.valueOf(msg.getType()));
        }else{
            accountVO.setType(AccountType.Normal);
        }

        accountVO.setPhoneStatus(ValidateStatus.Unvalidated);
        accountVO.setEmailStatus(ValidateStatus.Unvalidated);

        AccountExtraInfoVO ext = new AccountExtraInfoVO();
        ext.setUuid(accountVO.getUuid());
        ext.setCreateWay("register");
        if(msg.getGrade() != null){
            ext.setGrade(msg.getGrade());
        }else{
            ext.setGrade(AccountGrade.Normal);
        }

        ext.setUserUuid(msg.getUserUuid());

        ext.setCreateWay(msg.getSession().getType().toString());

        accountVO.setAccountExtraInfo(ext);

        accountVO = dbf.persistAndRefresh(accountVO);

        if (msg.getSession().isProxySession()) {
            ProxyAccountRefVO prevo = new ProxyAccountRefVO();
            prevo.setAccountUuid(msg.getAccountUuid());
            prevo.setCustomerAcccountUuid(accountVO.getUuid());

            dbf.persist(prevo);
        }

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

        if(msg.getSession().getType() == AccountType.SystemAdmin && msg.getUserType() != null){
            uservo.setUserType(msg.getUserType());
        }else{
            uservo.setUserType(UserType.normal);
        }

        if (msg.getPolicyUuid() != null) {
            Set<PolicyVO> policySet = new HashSet<>();
            PolicyVO policy = dbf.findByUuid(msg.getPolicyUuid(), PolicyVO.class);
            if (policy != null) {
                policySet.add(policy);
            }
            uservo.setPolicySet(policySet);
        }


        uservo = dbf.persistAndRefresh(uservo);

//        AccountApiSecurityVO api = new AccountApiSecurityVO();
//        api.setUuid(Platform.getUuid());
//        api.setAccountUuid(accountVO.getUuid());
//        api.setPrivateKey(getRandomString(36));
//        api.setPublicKey(getRandomString(36));
//        dbf.persist(api);

        APICreateUserEvent evt = new APICreateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(uservo));
        bus.publish(evt);
    }

    private void handle(APICreatePolicyMsg msg) {

        PolicyVO pvo = new PolicyVO();
        pvo.setUuid(Platform.getUuid());
        pvo.setName(msg.getName());
        pvo.setAccountUuid(msg.getAccountUuid());
        pvo.setDescription(msg.getDescription());

        Set<PermissionVO> permissionSet = new HashSet<PermissionVO>();

        for (String id  : msg.getPermissionUuids()) {
            PermissionVO permissionVO = dbf.findByUuid(id, PermissionVO.class);
            if (permissionVO != null ){
                permissionSet.add(permissionVO);
            }
        }

        pvo.setPermissionSet(permissionSet);

        pvo = dbf.persistAndRefresh(pvo);

        APICreatePolicyEvent evt = new APICreatePolicyEvent(msg.getId());

        evt.setInventory(PolicyInventory.valueOf(pvo));
        bus.publish(evt);
    }

    private void handle(APIDetachPolicyFromUserMsg msg) {
        SimpleQuery<UserPolicyRefVO> q = dbf.createQuery(UserPolicyRefVO.class);
        q.add(UserPolicyRefVO_.policyUuid, SimpleQuery.Op.EQ, msg.getPolicyUuid());
        q.add(UserPolicyRefVO_.userUuid, SimpleQuery.Op.EQ, msg.getUserUuid());
        UserPolicyRefVO ref = q.find();
        if (ref != null) {
            dbf.remove(ref);
        } else {
            throw new OperationFailureException(operr("noting to be found"));
        }

        APIDetachPolicyFromUserEvent evt = new APIDetachPolicyFromUserEvent(msg.getId());

        bus.publish(evt);
    }

    private void handle(APIDeletePolicyMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), PolicyVO.class);
        APIDeletePolicyEvent evt = new APIDeletePolicyEvent(msg.getId());

        bus.publish(evt);
    }

    private void handle(APIAttachPolicyToUserMsg msg) {
        APIAttachPolicyToUserEvent evt = new APIAttachPolicyToUserEvent(msg.getId());

        UserPolicyRefVO upvo = new UserPolicyRefVO();
        upvo.setPolicyUuid(msg.getPolicyUuid());
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

    private void handle(APICreatePermissionMsg msg) {

        PermissionVO auth = new PermissionVO();
        auth.setUuid(Platform.getUuid());
        auth.setPermission(msg.getPermission());
        auth.setName(msg.getName());
        auth.setSortId(msg.getSortId());
        auth.setType(msg.getType());
        auth.setAccountType(msg.getAccountType());

        APICreatePermissionEvent evt = new APICreatePermissionEvent(msg.getId());

        evt.setInventory(PermissionInventory.valueOf(dbf.persistAndRefresh(auth)));
        bus.publish(evt);
    }

    private void handle(APIUpdatePermissionMsg msg) {

        PermissionVO auth = dbf.findByUuid(msg.getUuid(), PermissionVO.class);

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

        APIUpdatePermisstionEvent evt = new APIUpdatePermisstionEvent(msg.getId());

        evt.setInventory(PermissionInventory.valueOf(auth));
        bus.publish(evt);

    }

    private void handle(APIDeletePermissionMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), PermissionVO.class);
        APICreatePermissionEvent evt = new APICreatePermissionEvent(msg.getId());

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
