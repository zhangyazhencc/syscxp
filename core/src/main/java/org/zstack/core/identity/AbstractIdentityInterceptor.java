package org.zstack.core.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQL;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.*;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.utils.*;
import org.zstack.utils.function.ForEachFunction;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Tuple;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxhread on 17/8/3.
 */
public abstract class AbstractIdentityInterceptor implements GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(AbstractIdentityInterceptor.class);

    @Autowired
    protected DatabaseFacade dbf;
    @Autowired
    protected ErrorFacade errf;
    @Autowired
    protected ThreadFacade thdf;
    @Autowired
    protected PluginRegistry pluginRgty;

    private List<String> resourceTypeForAccountRef;
    private List<Class> resourceTypes;

    protected Map<String, SessionInventory> sessions = new ConcurrentHashMap<>();


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

    public void checkApiMessagePermission(APIMessage msg) {
        new Auth().check(msg);
    }

    public boolean isAdmin(SessionInventory session) {
        return session.isAdminAccountSession();
    }

    public void init() {
        logger.debug("IdentiyInterceptor init.");
        try {
            buildResourceTypes();
            buildActions();
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    public void destroy() {
        logger.debug("IdentiyInterceptor destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
    }

    private void buildResourceTypes() throws ClassNotFoundException {
        resourceTypes = new ArrayList<>();
        for (String resrouceTypeName : resourceTypeForAccountRef) {
            Class<?> rs = Class.forName(resrouceTypeName);
            resourceTypes.add(rs);
            logger.debug(String.format("build resource type %s", resrouceTypeName));
        }
    }

    public abstract void removeExpiredSession(List<String> sessionUuids);

    private void startExpiredSessionCollector() {
        logger.debug("startExpiredSessionCollector");
        final int interval = CoreGlobalProperty.SESSION_CLEANUP_INTERVAL;
        expiredSessionCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            private List<String> deleteExpiredSessions() {
                List<String> uuids = new ArrayList<String>();
                Timestamp curr = getCurrentSqlDate();
                for (Map.Entry<String, SessionInventory> entry : sessions.entrySet()) {
                    SessionInventory sp = entry.getValue();
                    if (curr.after(sp.getExpiredDate())) {
                        uuids.add(sp.getUuid());
                    }
                }

                removeExpiredSession(uuids);

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
                    throw new CloudRuntimeException(String.format("@APIParam of %s.%s has checkAccount = true, however," + " the type of the field is not String or Collection but %s. " + "This field must be a resource UUID or a collection(e.g. List) of UUIDs", clz.getName(), f.getName(), f.getType()));
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
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.FRONT;
    }

    @Transactional(readOnly = true)
    public Timestamp getCurrentSqlDate() {
        return dbf.getCurrentSqlTime();
    }

    class Auth {
        APIMessage msg;
        SessionInventory session;
        MessageAction action;
        String username;

        void validate(APIMessage msg) {
            this.msg = msg;
            if (msg.getClass().isAnnotationPresent(SuppressCredentialCheck.class)) {
                suppressCredentialCheck();
            } else if (msg.getClass().isAnnotationPresent(InnerCredentialCheck.class)) {
                innerCredentialCheck();
            } else {
                action = actions.get(msg.getClass());

                sessionCheck();

                policyCheck();

                msg.setSession(session);
            }

        }

        void check(APIMessage msg) {
            this.msg = msg;
            if (msg.getClass().isAnnotationPresent(SuppressCredentialCheck.class)) {
                suppressCredentialCheck();
            } else if (msg.getClass().isAnnotationPresent(InnerCredentialCheck.class)) {
                innerCredentialCheck();
            } else {
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

                String sql = String.format("select uuid, accountUuid from %s where uuid in (:resourceUuids) ",
                        af.param.resourceType().getSimpleName());
                List<Tuple> ts = SQL.New(sql, Tuple.class)
                        .param("resourceUuids", resourceUuids)
                        .list();
                for (Tuple t : ts) {
                    String resourceUuid = t.get(0, String.class);
                    String resourceOwnerAccountUuid = t.get(1, String.class);
                    if (!session.getAccountUuid().equals(resourceOwnerAccountUuid)) {
                        throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("operation denied. The resource[uuid: %s, type: %s, ownerAccountUuid:%s] doesn't belong to the account[uuid: %s]", resourceUuid, af.param.resourceType().getSimpleName(), resourceOwnerAccountUuid, session.getAccountUuid())));
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("account-check pass. The resource[uuid: %s, type: %s] belongs to the account[uuid: %s]", resourceUuid, af.param.resourceType().getSimpleName(), session.getAccountUuid()));
                        }
                    }
                }
            }
        }

        private void useDecision(Decision d, boolean userPolicy) {
            String policyCategory = userPolicy ? "user policy" : "group policy";

            if (d.effect == StatementEffect.Allow) {
                logger.debug(String.format("API[name: %s, action: %s] is approved by a %s[name: %s, uuid: %s]," + " statement[action: %s]", msg.getClass().getSimpleName(), d.action, policyCategory, d.statement.getName(), d.statement.getUuid(), d.actionRule));
            } else {
                logger.debug(String.format("API[name: %s, action: %s] is denied by a %s[name: %s, uuid: %s]," + " statement[action: %s]", msg.getClass().getSimpleName(), d.action, policyCategory, d.statement.getName(), d.statement.getUuid(), d.actionRule));

                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("%s denied. user[name: %s, uuid: %s] is denied to execute API[%s]", policyCategory, username, session.getUuid(), msg.getClass().getSimpleName())));
            }
        }

        private void policyCheck() {
            if (session.isAdminAccountSession()) {
                return;
            }

            if (!session.isAdminUserSession()) {

                if (action.adminOnly) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("API[%s] is admin only", msg.getClass().getSimpleName())));
                }

                if (action.accountOnly && !session.isAccountSession()) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("API[%s] can only be called by an account, the current session is a user session[user uuid:%s]", msg.getClass().getSimpleName(), session.getUserUuid())));
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

            if (msg.getClass().isAnnotationPresent(SuppressUserCredentialCheck.class)){
                return;
            }

            List<PolicyStatement> userPolicys = session.getPolicyStatements();
            Decision d = decide(userPolicys);
            if (d != null) {
                useDecision(d, true);
                return;
            }

            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("user[uuid: %s] has no policy set for this operation, API[%s] is denied by default. You may either create policies for this user" + " or add the user into a group with polices set", session.getUserUuid(), msg.getClass().getSimpleName())));
        }

        class Decision {
            String action;
            PolicyStatement statement;
            String actionRule;
            StatementEffect effect;
        }

        private Decision decide(List<PolicyStatement> userPolicys) {
            for (String a : action.actions) {
                for (PolicyStatement s : userPolicys) {
                    for (String ac : s.getActions()) {
                        Pattern pattern = Pattern.compile(ac);
                        Matcher m = pattern.matcher(a);
                        boolean ret = m.matches();
                        if (ret) {
                            Decision d = new Decision();
                            d.action = a;
                            d.statement = s;
                            d.actionRule = ac;
                            d.effect = s.getEffect();
                            return d;
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("API[name: %s, action: %s] is not matched by policy[name: %s, uuid: %s" + ", statement[action: %s, effect: %s]", msg.getClass().getSimpleName(), a, s.getName(), s.getUuid(), ac, s.getEffect()));
                        }
                    }
                }
            }

            return null;
        }

        private void suppressCredentialCheck() {
            if (msg.getSession() != null && msg.getSession().getUuid() != null) {
                SessionInventory session = sessions.get(msg.getSession().getUuid());
                if (session != null) {
                    msg.setSession(session);
                }
            }
        }

        private void innerCredentialCheck() {
            if (InnerMessageHelper.validSignature(msg)) {
                return;
            }
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION,
                    String.format("The parameters of the message[%s] are inconsistent ", msg.getMessageName())
            ));
        }

        private void sessionCheck() {
            if (msg.getSession() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, String.format("session of message[%s] is null", msg.getMessageName())));
            }

            if (msg.getSession().getUuid() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "session uuid is null"));
            }

            session = sessions.get(msg.getSession().getUuid());
            if (session == null) {
                session = getSessionInventory(msg.getSession().getUuid());
                sessions.put(session.getUuid(), session);
            }

            Timestamp curr = getCurrentSqlDate();
            if (curr.after(session.getExpiredDate())) {
                logger.debug(String.format("session expired[%s < %s] for account[uuid:%s]", curr,
                        session.getExpiredDate(), session.getAccountUuid()));
                logOutSession(session.getUuid());
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
            }
        }
    }

    protected abstract SessionInventory getSessionInventory(String sessionUuid);

    protected abstract SessionInventory logOutSessionRemove(String sessionUuid);

    public void logOutSession(String sessionUuid) {

        SessionInventory session = sessions.get(sessionUuid);
        if (session == null) {
            session = logOutSessionRemove(sessionUuid);
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
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        new Auth().validate(msg);

        return msg;
    }


    public boolean isResourceHavingAccountReference(Class entityClass) {
        return resourceTypes.contains(entityClass);
    }

    public void setResourceTypeForAccountRef(List<String> resourceTypeForAccountRef) {
        this.resourceTypeForAccountRef = resourceTypeForAccountRef;
    }

    public Map<String, SessionInventory> getSessions() {
        return sessions;
    }

    public SessionInventory getSession(String sessionUuid) {
        return sessions.get(sessionUuid);
    }

}
