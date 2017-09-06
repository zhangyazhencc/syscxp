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

import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import java.util.HashSet;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;

import java.util.Set;

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
        AccountVO cont = dbf.findByUuid(msg.getTargetUuid(), AccountVO.class);

        String pwd = getRandomString(8);

        cont.setPassword(DigestUtils.sha512Hex(pwd));
        dbf.updateAndRefresh(cont);

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

        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(),UserVO.class);
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
        acvo.setEmail(msg.getPhone());
        acvo.setNoticeWay(msg.getNoticeWay());

        APICreateAccountContactsEvent evt = new APICreateAccountContactsEvent(msg.getId());
        evt.setInventory(AccountContactsInventory.valueOf(dbf.persistAndRefresh(acvo)));
        bus.publish(evt);
    }

    private void handle(APIDeleteAccountContactsMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), AccountContactsVO.class);
        APIDeletePolicyEvent evt = new APIDeletePolicyEvent(msg.getId());

        bus.publish(evt);
    }

    private void handle(APIUpdateAccountContactsMsg msg) {
        APIUpdateAccountContactsEvent evt = new APIUpdateAccountContactsEvent(msg.getId());
        AccountContactsVO cont = dbf.findByUuid(msg.getUuid(), AccountContactsVO.class);

        SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
        q.add(ProxyAccountRefVO_.customerAcccountUuid, SimpleQuery.Op.EQ, msg.getUuid());
        ProxyAccountRefVO acvo = q.find();

        if (!msg.getSession().getType().equals(AccountType.SystemAdmin) &&
                !cont.getAccountUuid().equals(msg.getAccountUuid()) &&
                (acvo == null || !msg.getAccountUuid().equals(acvo.getAccountUuid()))
                ) {
            throw new OperationFailureException(operr("account[uuid: %s] is a normal account, " +
                            "it cannot update the infomation of another account[uuid: %s]",
                    msg.getAccountUuid(), msg.getUuid()));
        }


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
                evt.setPhone(msg.getPhone());

                account.setPhone(msg.getPhone());
                account.setPhoneStatus(ValidateStatus.Validated);
                dbf.updateAndRefresh(account);
            }
        }

        bus.publish(evt);
    }

    private void handle(APIUserPhoneAuthenticationMsg msg) {

        APIUserPhoneAuthenticationEvent evt = new APIUserPhoneAuthenticationEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);

        if (user.getPhoneStatus() == ValidateStatus.Unvalidated && smsService.validateVerificationCode(msg.getPhone(), msg.getCode())) {

            evt.setPhone(msg.getPhone());

            user.setPhone(msg.getPhone());
            user.setPhoneStatus(ValidateStatus.Validated);
            dbf.updateAndRefresh(user);
        }else{
            throw new ApiMessageInterceptionException(argerr("Validation code does not match[uuid: %s]",
                    msg.getSession().getAccountUuid()));
        }

        bus.publish(evt);
    }

    private void handle(APIUpdateUserPWDMsg msg) {

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        UserVO user = dbf.findByUuid(msg.getSession().getUserUuid(), UserVO.class);
        if (user.getPassword().equals(msg.getOldpassword())) {
            user.setPassword(msg.getNewpassword());
            evt.setInventory(UserInventory.valueOf(dbf.updateAndRefresh(user)));
        } else {
            throw new CloudRuntimeException("bad old passwords");
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

        boolean accountis = false;
        if (msg.getCompany() != null) {
            account.setCompany(msg.getCompany());
            accountis = true;
        }
        if (msg.getDescription() != null) {
            account.setDescription(msg.getDescription());
            accountis = true;
        }
        if (msg.getEmail() != null) {
            account.setEmail(msg.getEmail());
            accountis = true;
        }

        if (msg.getPhone() != null) {
            account.setPhone(msg.getPhone());
            accountis = true;
        }
        if (msg.getStatus() != null) {
            account.setStatus(msg.getStatus().equals(AccountStatus.Available)
                    ? AccountStatus.Available : AccountStatus.Disabled);
            accountis = true;
        }
        if (msg.getTrueName() != null) {
            account.setTrueName(msg.getTrueName());
            accountis = true;
        }

        if (msg.getType() != null) {
            if (msg.getType().equals(AccountType.Normal)) {
                account.setType(AccountType.Normal);
            } else if (msg.getType().equals(AccountType.Proxy)) {
                account.setType(AccountType.Proxy);
            } else if (msg.getType().equals(AccountType.SystemAdmin)) {
                account.setType(AccountType.SystemAdmin);
            }
            accountis = true;
        }

        if (msg.getIndustry() != null) {
            account.setIndustry(msg.getIndustry());
            accountis = true;
        }

        if (accountis) {
            account = dbf.updateAndRefresh(account);
        }


        AccountExtraInfoVO aeivo = dbf.findByUuid(msg.getUuid(), AccountExtraInfoVO.class);
        if (aeivo == null) {
            aeivo = new AccountExtraInfoVO();
        }
        boolean done = false;
        if (msg.getGrade() != null) {
            if (msg.getGrade().equals(AccountGrade.Normal)) {
                aeivo.setGrade(AccountGrade.Normal);
            } else if (msg.getGrade().equals(AccountGrade.Middling)) {
                aeivo.setGrade(AccountGrade.Middling);
            } else if (msg.getGrade().equals(AccountGrade.Important)) {
                aeivo.setGrade(AccountGrade.Important);
            } else {
                throw new OperationFailureException(operr("Parameter is not legal(grade)"));
            }
            done = true;
        }

        if (msg.getSalesman() != null) {
            aeivo.setSalesman(msg.getSalesman());
            done = true;
        }

        if (done) {
            if (aeivo.getUuid() != null) {
                aeivo = dbf.updateAndRefresh(aeivo);
            } else {
                aeivo.setUuid(Platform.getUuid());
                aeivo.setAccountUuid(account.getUuid());
                aeivo = dbf.persistAndRefresh(aeivo);
            }
        }

        APIUpdateAccountEvent evt = new APIUpdateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(account, aeivo));
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
            user.setPassword(msg.getDepartment());
            update = true;
        }
        if (msg.getEmail() != null) {
            user.setEmail(msg.getEmail());
            update = true;
        }
        if (msg.getPhone() != null) {
            user.setPhone(msg.getPhone());
            update = true;
        }
        if (msg.getStatus() != null) {
            user.setStatus(msg.getStatus().equals(AccountStatus.Available)
                    ? AccountStatus.Available : AccountStatus.Disabled);
            update = true;
        }
        if (msg.getTrueName() != null) {
            user.setTrueName(msg.getTrueName());
            update = true;
        }

        if (update) {
            user = dbf.updateAndRefresh(user);
        }


        if (msg.getPolicyUuid() != null) {
            SimpleQuery<UserPolicyRefVO> q = dbf.createQuery(UserPolicyRefVO.class);
            q.add(UserPolicyRefVO_.userUuid, SimpleQuery.Op.EQ, msg.getUuid());
            UserPolicyRefVO uprvo = q.find();

            Set<PolicyVO> policy = new HashSet<>();
            policy.add(dbf.findByUuid(msg.getPolicyUuid(),PolicyVO.class));
            user.setPolicy(policy);

            if (uprvo != null) {
                uprvo.setPolicyUuid(msg.getPolicyUuid());
                dbf.update(uprvo);
            } else {
                uprvo = new UserPolicyRefVO();
                uprvo.setPolicyUuid(msg.getPolicyUuid());
                uprvo.setUserUuid(msg.getUuid());
                dbf.persist(uprvo);
            }
        }

        APIUpdateUserEvent evt = new APIUpdateUserEvent(msg.getId());
        evt.setInventory(UserInventory.valueOf(user));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateAccountMsg msg) {

        AccountVO accountvo = new AccountVO();

        accountvo.setUuid(Platform.getUuid());
        accountvo.setName(msg.getName());
        accountvo.setPassword(msg.getPassword());
        accountvo.setCompany(msg.getCompany());
        accountvo.setDescription(msg.getDescription());
        accountvo.setEmail(msg.getEmail());
        accountvo.setIndustry(msg.getIndustry());
        accountvo.setPhone(msg.getPhone());
        accountvo.setTrueName(msg.getTrueName());
        accountvo.setStatus(AccountStatus.Available);
        if(msg.getType() != null){
            accountvo.setType(msg.getType().equals("Proxy")?AccountType.Proxy:AccountType.Normal);
        }else{
            accountvo.setType(AccountType.Normal);
        }

        accountvo.setPhoneStatus(ValidateStatus.Unvalidated);
        accountvo.setEmailStatus(ValidateStatus.Unvalidated);

        accountvo = dbf.persistAndRefresh(accountvo);


        AccountExtraInfoVO aeivo = new AccountExtraInfoVO();
        aeivo.setUuid(Platform.getUuid());
        aeivo.setAccountUuid(accountvo.getUuid());
        if(msg.getGrade() != null){
            aeivo.setGrade(msg.getGrade());
        }
        if (msg.getUserUuid() != null) {
            aeivo.setSalesman(msg.getUserUuid());
        }
        aeivo.setCreateWay(msg.getSession().getType().toString());
        aeivo = dbf.persistAndRefresh(aeivo);

        AccountApiSecurityVO api = new AccountApiSecurityVO();
        api.setUuid(Platform.getUuid());
        api.setAccountUuid(accountvo.getUuid());
        api.setPrivateKey(getRandomString(36));
        api.setPublicKey(getRandomString(36));
        dbf.persist(api);

        if (msg.getSession().isProxySession()) {
            ProxyAccountRefVO prevo = new ProxyAccountRefVO();
            prevo.setAccountUuid(msg.getAccountUuid());
            prevo.setCustomerAcccountUuid(accountvo.getUuid());

            dbf.persist(prevo);
        }

        APICreateAccountEvent evt = new APICreateAccountEvent(msg.getId());
        evt.setInventory(AccountInventory.valueOf(accountvo, aeivo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateUserMsg msg) {

        UserVO uservo = new UserVO();

        uservo.setUuid(Platform.getUuid());
        uservo.setAccountUuid(msg.getAccountUuid());
        uservo.setDepartment(msg.getDepartment());
        uservo.setDescription(msg.getDescription());
        uservo.setEmail(msg.getEmail());
        uservo.setName(msg.getName());
        uservo.setPassword(msg.getPassword());
        uservo.setPhone(msg.getPhone());
        uservo.setStatus(AccountStatus.Available);
        uservo.setTrueName(msg.getTrueName());

        uservo.setEmailStatus(ValidateStatus.Unvalidated);
        uservo.setPhoneStatus(ValidateStatus.Unvalidated);

        uservo = dbf.persistAndRefresh(uservo);

        UserPolicyRefVO uprv = new UserPolicyRefVO();
        if (msg.getPolicyUuid() != null) {
            uprv.setUserUuid(uservo.getUuid());
            uprv.setPolicyUuid(msg.getPolicyUuid());
            dbf.persist(uprv);

            Set<PolicyVO> policy = new HashSet<>();
            policy.add(dbf.findByUuid(msg.getPolicyUuid(),PolicyVO.class));
            uservo.setPolicy(policy);

        }

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
        pvo.setPolicyStatement(JSONObjectUtil.toJsonString(msg.getStatements()));

        APICreatePolicyEvent evt = new APICreatePolicyEvent(msg.getId());

        evt.setInventory(PolicyInventory.valueOf(dbf.persistAndRefresh(pvo)));
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
        if (!StringUtils.isEmpty(msg.getAccountType())) {
            auth.setAccountType(msg.getAccountType());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPermission())) {
            auth.setPermission(msg.getPermission());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getType())) {
            auth.setType(msg.getType());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getSortId())) {
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
