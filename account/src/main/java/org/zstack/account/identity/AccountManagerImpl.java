package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.*;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.StatementEffect;
import org.zstack.header.identity.PolicyStatement;
import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.SessionLogoutExtensionPoint;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.*;
import org.zstack.utils.*;
import org.zstack.utils.function.ForEachFunction;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zstack.header.identity.*;
import org.zstack.account.header.identity.*;
import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;
/**
 * Created by zxhread on 17/8/3.
 */
public class AccountManagerImpl extends AbstractService implements AccountManager, PrepareDbInitialValueExtensionPoint,
        GlobalApiMessageInterceptor, ApiMessageInterceptor {
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
    private EventFacade evtf;
    @Autowired
    private GlobalConfigFacade gcf;

    private List<String> resourceTypeForAccountRef;
    private List<Class> resourceTypes;
    private Map<String, SessionInventory> sessions = new ConcurrentHashMap<>();


    class AccountCheckField {
        Field field;
        APIParam param;
    }

    class MessageAction {
        boolean adminOnly;
        List<String> actions;
        String category;
        boolean accountOnly;
        List<AccountCheckField> accountCheckFields;
        boolean accountControl;
    }

    private Map<Class, MessageAction> actions = new HashMap<>();
    private Future<Void> expiredSessionCollector;

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
        new Auth().check(msg);
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
        if (msg instanceof APICreateAccountMsg) {
            handle((APICreateAccountMsg) msg);
        } else if (msg instanceof APIListAccountMsg) {
            handle((APIListAccountMsg) msg);
        } else if (msg instanceof APIListUserMsg) {
            handle((APIListUserMsg) msg);
        } else if (msg instanceof APILogInByAccountMsg) {
            handle((APILogInByAccountMsg) msg);
        } else if (msg instanceof APILogInByUserMsg) {
            handle((APILogInByUserMsg) msg);
        } else if (msg instanceof APILogOutMsg) {
            handle((APILogOutMsg) msg);
        } else if (msg instanceof APIValidateSessionMsg) {
            handle((APIValidateSessionMsg) msg);
        } else if (msg instanceof APICheckApiPermissionMsg) {
            handle((APICheckApiPermissionMsg) msg);
        } else {
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
                    new Auth().check(api);
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

        SessionInventory s = sessions.get(msg.getSessionUuid());
        Timestamp current = dbf.getCurrentSqlTime();
        boolean valid = true;

        if (s != null) {
            if (current.after(s.getExpiredDate())) {
                valid = false;
                logOutSession(s.getUuid());
            }
        } else {
            SessionVO session = dbf.findByUuid(msg.getSessionUuid(), SessionVO.class);
            if (session != null && current.after(session.getExpiredDate())) {
                valid = false;
                logOutSession(session.getUuid());
            } else if (session == null) {
                valid = false;
            }
        }

        reply.setValidSession(valid);
        bus.reply(msg, reply);
    }


    private void handle(APILogOutMsg msg) {
        APILogOutReply reply = new APILogOutReply();
        logOutSession(msg.getSessionUuid());
        bus.reply(msg, reply);
    }

    private SessionInventory getSession(String accountUuid, AccountType type, String userUuid) {
        int maxLoginTimes = IdentityGlobalConfig.MAX_CONCURRENT_SESSION.value(Integer.class);
        SimpleQuery<SessionVO> query = dbf.createQuery(SessionVO.class);
        query.add(SessionVO_.accountUuid, Op.EQ, accountUuid);
        query.add(SessionVO_.userUuid, Op.EQ, userUuid);
        long count = query.count();
        if (count >= maxLoginTimes) {
            String err = String.format("Login sessions hit limit of max allowed concurrent login sessions, max allowed: %s", maxLoginTimes);
            throw new BadCredentialsException(err);
        }

        int sessionTimeout = IdentityGlobalConfig.SESSION_TIMEOUT.value(Integer.class);
        SessionVO svo = new SessionVO();
        svo.setUuid(Platform.getUuid());
        svo.setAccountUuid(accountUuid);
        svo.setUserUuid(userUuid);
        svo.setType(type);
        long expiredTime = getCurrentSqlDate().getTime() + TimeUnit.SECONDS.toMillis(sessionTimeout);
        svo.setExpiredDate(new Timestamp(expiredTime));
        svo = dbf.persistAndRefresh(svo);
        SessionInventory session = svo.toSessionInventory();
        sessions.put(session.getUuid(), session);
        return session;
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

        reply.setInventory(getSession(user.getAccountUuid(), account.getType(), user.getUuid()));
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

        reply.setInventory(getSession(vo.getUuid(), vo.getType(), vo.getUuid()));
        bus.reply(msg, reply);
    }

    private void handle(APIListUserMsg msg) {
//        List<UserVO> vos = dl.listByApiMessage(msg, UserVO.class);
//        List<UserInventory> invs = UserInventory.valueOf(vos);
//        APIListUserReply reply = new APIListUserReply();
//        reply.setInventories(invs);
//        bus.reply(msg, reply);
    }

    private void handle(APIListAccountMsg msg) {
        List<AccountVO> vos = dl.listByApiMessage(msg, AccountVO.class);
        List<AccountInventory> invs = AccountInventory.valueOf(vos);
        APIListAccountReply reply = new APIListAccountReply();
        reply.setInventories(invs);
        bus.reply(msg, reply);
    }

    private void handle(APICreateAccountMsg msg) {
        final AccountInventory inv = new SQLBatchWithReturn<AccountInventory>() {
            @Override
            protected AccountInventory scripts() {
                AccountVO vo = new AccountVO();

                vo.setUuid(Platform.getUuid());

                vo.setName(msg.getName());
                vo.setPassword(msg.getPassword());
                vo.setType(msg.getType() != null ? AccountType.valueOf(msg.getType()) : AccountType.Normal);
                persist(vo);
                reload(vo);

                return AccountInventory.valueOf(vo);
            }
        }.execute();


        CollectionUtils.safeForEach(pluginRgty.getExtensionList(AfterCreateAccountExtensionPoint.class),
                arg -> arg.afterCreateAccount(inv));

        APICreateAccountEvent evt = new APICreateAccountEvent(msg.getId());
        evt.setInventory(inv);
        bus.publish(evt);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AccountConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        try {
            buildResourceTypes();
            buildActions();
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }

    private void buildResourceTypes() throws ClassNotFoundException {
        resourceTypes = new ArrayList<>();
        for (String resrouceTypeName : resourceTypeForAccountRef) {
            Class<?> rs = Class.forName(resrouceTypeName);
            resourceTypes.add(rs);
        }
    }

    private void startExpiredSessionCollector() {
        final int interval = IdentityGlobalConfig.SESSION_CLEANUP_INTERVAL.value(Integer.class);
        expiredSessionCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            @Transactional
            private List<String> deleteExpiredSessions() {
                String sql = "select s.uuid from SessionVO s where CURRENT_TIMESTAMP  >= s.expiredDate";
                TypedQuery<String> q = dbf.getEntityManager().createQuery(sql, String.class);
                List<String> uuids = q.getResultList();
                if (!uuids.isEmpty()) {
                    String dsql = "delete from SessionVO s where s.uuid in :uuids";
                    Query dq = dbf.getEntityManager().createQuery(dsql);
                    dq.setParameter("uuids", uuids);
                    dq.executeUpdate();
                }
                return uuids;
            }

            @Override
            public void run() {
                List<String> uuids = deleteExpiredSessions();
                for (String uuid : uuids) {
                    sessions.remove(uuid);
                }
            }

            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override
            public long getInterval() {
                return interval;
            }

            @Override
            public String getName() {
                return "ExpiredSessionCleanupThread";
            }

        });
    }

    private void buildActions() {
        List<Class> apiMsgClasses = BeanUtils.scanClassByType("org.zstack", APIMessage.class);
        for (Class clz : apiMsgClasses) {
            Action a = (Action) clz.getAnnotation(Action.class);
            if (a == null) {
                logger.debug(String.format("API message[%s] doesn't have annotation @Action, assume it's an admin only API", clz));
                MessageAction ma = new MessageAction();
                ma.adminOnly = true;
                ma.accountOnly = true;
                ma.accountControl = false;
                actions.put(clz, ma);
                continue;
            }

            MessageAction ma = new MessageAction();
            ma.accountOnly = a.accountOnly();
            ma.adminOnly = a.adminOnly();
            ma.category = a.category();
            ma.actions = new ArrayList<String>();
            ma.accountControl = a.accountControl();
            ma.accountCheckFields = new ArrayList<AccountCheckField>();
            for (String ac : a.names()) {
                ma.actions.add(String.format("%s:%s", ma.category, ac));
            }

            List<Field> allFields = FieldUtils.getAllFields(clz);
            for (Field f : allFields) {
                APIParam at = f.getAnnotation(APIParam.class);
                if (at == null || !at.checkAccount()) {
                    continue;
                }

                if (!String.class.isAssignableFrom(f.getType()) && !Collection.class.isAssignableFrom(f.getType())) {
                    throw new CloudRuntimeException(String.format("@APIParam of %s.%s has checkAccount = true, however," +
                                    " the type of the field is not String or Collection but %s. " +
                                    "This field must be a resource UUID or a collection(e.g. List) of UUIDs",
                            clz.getName(), f.getName(), f.getType()));
                }

                AccountCheckField af = new AccountCheckField();
                f.setAccessible(true);
                af.field = f;
                af.param = at;
                ma.accountCheckFields.add(af);
            }

            ma.actions.add(String.format("%s:%s", ma.category, clz.getName()));
            ma.actions.add(String.format("%s:%s", ma.category, clz.getSimpleName()));

            actions.put(clz, ma);
        }
    }

    @Override
    public boolean stop() {
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
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
                vo.setEmail(AccountConstant.INITIAL_SYSTEM_ADMIN_EMAIL);
                vo.setType(AccountType.SystemAdmin);
                vo.setStatus(AccountStatus.Available);

                dbf.persist(vo);
                logger.debug(String.format("Created initial system admin account[name:%s]", AccountConstant.INITIAL_SYSTEM_ADMIN_NAME));
            }
        } catch (Exception e) {
            throw new CloudRuntimeException("Unable to create default system admin account", e);
        }
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.FRONT;
    }

    private void logOutSession(String sessionUuid) {
        SessionInventory session = sessions.get(sessionUuid);
        if (session == null) {
            SessionVO svo = dbf.findByUuid(sessionUuid, SessionVO.class);
            session = svo == null ? null : svo.toSessionInventory();
        }

        if (session == null) {
            return;
        }

        final SessionInventory finalSession = session;
        CollectionUtils.safeForEach(pluginRgty.getExtensionList(SessionLogoutExtensionPoint.class),
                new ForEachFunction<SessionLogoutExtensionPoint>() {
                    @Override
                    public void run(SessionLogoutExtensionPoint ext) {
                        ext.sessionLogout(finalSession);
                    }
                });

        sessions.remove(sessionUuid);
        dbf.removeByPrimaryKey(sessionUuid, SessionVO.class);
    }

    @Transactional(readOnly = true)
    private Timestamp getCurrentSqlDate() {
        Query query = dbf.getEntityManager().createNativeQuery("select current_timestamp()");
        return (Timestamp) query.getSingleResult();
    }

    class Auth {
        APIMessage msg;
        SessionInventory session;
        MessageAction action;
        String username;

        void validate(APIMessage msg) {
            this.msg = msg;
            if (msg.getClass().isAnnotationPresent(SuppressCredentialCheck.class)) {
                return;
            }else{
                action = actions.get(msg.getClass());

                sessionCheck();

                policyCheck();

                msg.setSession(session);
            }

        }

        void check(APIMessage msg) {
            this.msg = msg;
            if (msg.getClass().isAnnotationPresent(SuppressCredentialCheck.class)) {
                return;
            }else {
                DebugUtils.Assert(msg.getSession() != null, "session cannot be null");
                session = msg.getSession();

                action = actions.get(msg.getClass());
                policyCheck();
            }

        }

        private void accountFieldCheck() throws IllegalAccessException {
            for (AccountCheckField af : action.accountCheckFields) {
                Object value = af.field.get(msg);
                if (value == null) {
                    continue;
                }

                Set resourceUuids = new HashSet();

                if (String.class.isAssignableFrom(af.field.getType())) {
                    resourceUuids.add(value);
                } else if (Collection.class.isAssignableFrom(af.field.getType())) {
                    resourceUuids.addAll((Collection) value);

                }

                if (resourceUuids.isEmpty()) {
                    return;
                }

                List<Tuple> ts = SQL.New(
                        " select uuid, accountUuid from :resourceType where uuid in (:resourceUuids) ",Tuple.class)
                        .param("resourceType", af.param.resourceType().getSimpleName())
                        .param("resourceUuids", resourceUuids)
                        .list();
                for (Tuple t : ts) {
                    String resourceUuid = t.get(0, String.class);
                    String resourceOwnerAccountUuid = t.get(1, String.class);
                    if (!session.getAccountUuid().equals(resourceOwnerAccountUuid)) {
                        throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED,
                                String.format("operation denied. The resource[uuid: %s, type: %s, ownerAccountUuid:%s] doesn't belong to the account[uuid: %s]",
                                        resourceUuid, af.param.resourceType().getSimpleName(), resourceOwnerAccountUuid, session.getAccountUuid())
                        ));
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("account-check pass. The resource[uuid: %s, type: %s] belongs to the account[uuid: %s]",
                                    resourceUuid, af.param.resourceType().getSimpleName(), session.getAccountUuid()));
                        }
                    }
                }
            }
        }

        private void useDecision(Decision d, boolean userPolicy) {
            String policyCategory = userPolicy ? "user policy" : "group policy";

            if (d.effect == StatementEffect.Allow) {
                logger.debug(String.format("API[name: %s, action: %s] is approved by a %s[name: %s, uuid: %s]," +
                                " statement[action: %s]", msg.getClass().getSimpleName(), d.action,
                        policyCategory, d.policy.getName(), d.policy.getUuid(), d.actionRule));
            } else {
                logger.debug(String.format("API[name: %s, action: %s] is denied by a %s[name: %s, uuid: %s]," +
                                " statement[action: %s]", msg.getClass().getSimpleName(), d.action,
                        policyCategory, d.policy.getName(), d.policy.getUuid(), d.actionRule));

                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED,
                        String.format("%s denied. user[name: %s, uuid: %s] is denied to execute API[%s]",
                                policyCategory, username, session.getUuid(), msg.getClass().getSimpleName())
                ));
            }
        }

        private void policyCheck() {
            if (session.isAdminAccountSession()) {
                return;
            }

            if (!session.isAdminUserSession()) {

                if (action.adminOnly) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED,
                            String.format("API[%s] is admin only", msg.getClass().getSimpleName())));
                }

                if (action.accountOnly && !session.isAccountSession()) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED,
                            String.format("API[%s] can only be called by an account, the current session is a user session[user uuid:%s]",
                                    msg.getClass().getSimpleName(), session.getUserUuid())
                    ));
                }

                if (action.accountCheckFields != null && !action.accountCheckFields.isEmpty()) {
                    try {
                        accountFieldCheck();
                    } catch (ApiMessageInterceptionException ae) {
                        throw ae;
                    } catch (Exception e) {
                        throw new CloudRuntimeException(e);
                    }
                }

                if (session.isAccountSession()) {
                    return;
                }
            }

            SimpleQuery<UserVO> uq = dbf.createQuery(UserVO.class);
            uq.select(UserVO_.name);
            uq.add(UserVO_.uuid, Op.EQ, session.getUserUuid());
            username = uq.findValue();

            List<PolicyInventory> userPolicys = getUserPolicys();
            Decision d = decide(userPolicys);
            if (d != null) {
                useDecision(d, true);
                return;
            }

            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED,
                    String.format("user[name: %s, uuid: %s] has no policy set for this operation, API[%s] is denied by default. You may either create policies for this user" +
                            " or add the user into a group with polices set", username, session.getUserUuid(), msg.getClass().getSimpleName())
            ));
        }

        class Decision {
            PolicyInventory policy;
            String action;
            PolicyStatement statement;
            String actionRule;
            StatementEffect effect;
        }

        private Decision decide(List<PolicyInventory> userPolicys) {
            for (String a : action.actions) {
                for (PolicyInventory p : userPolicys) {
                    for (PolicyStatement s : p.getStatements()) {
                        for (String ac : s.getActions()) {
                            Pattern pattern = Pattern.compile(ac);
                            Matcher m = pattern.matcher(a);
                            boolean ret = m.matches();
                            if (ret) {
                                Decision d = new Decision();
                                d.policy = p;
                                d.action = a;
                                d.statement = s;
                                d.actionRule = ac;
                                d.effect = s.getEffect();
                                return d;
                            }

                            if (logger.isTraceEnabled()) {
                                logger.trace(String.format("API[name: %s, action: %s] is not matched by policy[name: %s, uuid: %s" +
                                                ", statement[action: %s, effect: %s]", msg.getClass().getSimpleName(),
                                        a, p.getName(), p.getUuid(), ac, s.getEffect()));
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Transactional(readOnly = true)
        private List<PolicyInventory> getUserPolicys() {
            String sql = "select p from PolicyVO p, UserPolicyRefVO ref where ref.userUuid = :uuid and ref.policyUuid = p.uuid";
            TypedQuery<PolicyVO> q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
            q.setParameter("uuid", session.getUserUuid());
            return PolicyInventory.valueOf(q.getResultList());
        }

        private void sessionCheck() {
            if (msg.getSession() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION,
                        String.format("session of message[%s] is null", msg.getMessageName())));
            }

            if (msg.getSession().getUuid() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION,
                        "session uuid is null"));
            }

            SessionInventory session = sessions.get(msg.getSession().getUuid());
            if (session == null) {
                SessionVO svo = dbf.findByUuid(msg.getSession().getUuid(), SessionVO.class);
                if (svo == null) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION,
                            "Session expired"));
                }
                session = svo.toSessionInventory();
                sessions.put(session.getUuid(), session);
            }

            Timestamp curr = getCurrentSqlDate();
            if (curr.after(session.getExpiredDate())) {
                logger.debug(String.format("session expired[%s < %s] for account[uuid:%s]", curr,
                        session.getExpiredDate(), session.getAccountUuid()));
                logOutSession(session.getUuid());
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
            }

            this.session = session;
        }
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        new Auth().validate(msg);

        if (msg instanceof APIUpdateAccountMsg) {
            validate((APIUpdateAccountMsg) msg);
        } else if (msg instanceof APICreateAccountMsg) {
            validate((APICreateAccountMsg) msg);
        } else if (msg instanceof APICreateUserMsg) {
            validate((APICreateUserMsg) msg);
        } else if (msg instanceof APILogInByUserMsg) {
            validate((APILogInByUserMsg) msg);
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


    private void validate(APIUpdateAccountMsg msg) {
        AccountVO a = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountVO.class);
        if (msg.getUuid() == null) {
            msg.setUuid(msg.getSession().getAccountUuid());
        }


        if (a.getType() == AccountType.SystemAdmin) {
            if (msg.getName() != null && msg.getUuid() == null ) {
                throw new OperationFailureException(operr(
                        "the name of admin account cannot be updated"
                ));
            }

            return;
        }

        AccountVO account = dbf.findByUuid(msg.getUuid(), AccountVO.class);
        if (!account.getUuid().equals(a.getUuid())) {
            throw new OperationFailureException(operr("account[uuid: %s, name: %s] is a normal account, it cannot reset the password of another account[uuid: %s]",
                    account.getUuid(), account.getName(), msg.getUuid()));
        }
    }

    private void setServiceId(APIMessage msg) {
        if (msg instanceof AccountMessage) {
            AccountMessage amsg = (AccountMessage) msg;
            bus.makeTargetServiceIdByResourceUuid(msg, AccountConstant.SERVICE_ID, amsg.getAccountUuid());
        }
    }

    @Override
    public boolean isResourceHavingAccountReference(Class entityClass) {
        return resourceTypes.contains(entityClass);
    }

    public void setResourceTypeForAccountRef(List<String> resourceTypeForAccountRef) {
        this.resourceTypeForAccountRef = resourceTypeForAccountRef;
    }

}
