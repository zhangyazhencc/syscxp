package org.zstack.billing.identity;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.zstack.billing.header.identity.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.BeanUtils;
import org.zstack.utils.DebugUtils;
import org.zstack.utils.FieldUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;
import org.zstack.header.identity.*;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

public class BillingManagerImpl extends AbstractService implements BillingManager, GlobalApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BillingManagerImpl.class);

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
    @Autowired
    private RESTFacade restf;

    private List<Class> resourceTypes;
    private static Map<String, SessionInventory> sessions = new ConcurrentHashMap<>();
    private List<String> resourceTypeForAccountRef;

    private Future<Void> expiredSessionCollector;

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
        if (msg instanceof APIGetAccountBalanceMsg) {
            handle((APIGetAccountBalanceMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }


    private void handle(APIGetAccountBalanceMsg msg) {
        if (msg.getUuid() != null && !"".equals(msg.getUuid())) {
            SimpleQuery<AccountBalanceVO> q = dbf.createQuery(AccountBalanceVO.class);
            q.add(AccountBalanceVO_.uuid, Op.EQ, msg.getUuid());
            AccountBalanceVO a = q.find();
            AccountBalanceInventory inventory = new AccountBalanceInventory();
            if (a != null) {
                inventory.setUuid(a.getUuid());
                inventory.setCashBalance(a.getCashBalance());
                inventory.setPresentBalance(a.getPresentBalance());
                inventory.setCreditPoint(a.getCreditPoint());
            }

            APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
            reply.setInventory(inventory);
            bus.reply(msg, reply);
        }
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID);
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

    private void startExpiredSessionCollector() {
        final int interval = BillingGlobalConfig.SESSION_CLEANUP_INTERVAL.value(Integer.class);
        expiredSessionCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            @Override
            public void run() {
                long currentMillis = System.currentTimeMillis();
                for (String key : sessions.keySet()) {
                    SessionInventory si = sessions.get(key);
                    if (si.getExpiredDate().getTime() >= currentMillis) {
                        sessions.remove(key);
                    }
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


    private void buildResourceTypes() throws ClassNotFoundException {
        resourceTypes = new ArrayList<>();
        for (String resourceTypeName : resourceTypeForAccountRef) {
            Class<?> rs = Class.forName(resourceTypeName);
            resourceTypes.add(rs);
        }
    }


    private void buildActions() {
        List<Class> apiMsgClasses = BeanUtils.scanClassByType("org.zstack", APIMessage.class);
        for (Class<APIMessage> clz : apiMsgClasses) {
            Action a = clz.getAnnotation(Action.class);
            if (a == null) {
                logger.debug(String.format("API message[%s] doesn't have annotation @Action, assume it's an admin only API", clz));
                MessageAction ma = new MessageAction();
                ma.adminOnly = true;
                ma.accountOnly = true;
                ma.accountControl = false;
                actions.put(clz, ma);
            } else {
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
                        return;
                    } else {
                        if (!String.class.isAssignableFrom(f.getType()) && !Collection.class.isAssignableFrom(f.getType())) {
                            throw new CloudRuntimeException(String.format("@APIParam of %s.%s has checkAccount = true, however," + " the type of the field is not String or Collection but %s. " + "This field must be a resource UUID or a collection(e.g. List) of UUIDs", clz.getName(), f.getName(), f.getType()));
                        }

                        AccountCheckField af = new AccountCheckField();
                        f.setAccessible(true);
                        af.field = f;
                        af.param = at;
                        ma.accountCheckFields.add(af);
                    }


                }

                ma.actions.add(String.format("%s:%s", ma.category, clz.getName()));
                ma.actions.add(String.format("%s:%s", ma.category, clz.getSimpleName()));

                actions.put(clz, ma);
            }


        }
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIGetAccountBalanceMsg) {
            validate((APIGetAccountBalanceMsg) msg);
        }
        return msg;
    }

    private void validate(APIGetAccountBalanceMsg msg) {
        if (msg.getUuid() == null || "".equals(msg.getUuid())) {
            throw new ApiMessageInterceptionException(Platform.argerr("%s must be not null", "uuid"));
        }
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return null;
    }

    @Override
    public void checkApiMessagePermission(APIMessage msg) {
        new Auth().check(msg);
    }

    @Override
    public boolean isAdmin(SessionInventory session) {
        return false;
    }

    @Override
    public boolean isResourceHavingAccountReference(Class entityClass) {
        return resourceTypes.contains(entityClass);
    }

    public void setResourceTypeForAccountRef(List resourceTypeForAccountRef) {
        this.resourceTypeForAccountRef = resourceTypeForAccountRef;
    }

    public List getResourceTypeForAccountRef() {
        return resourceTypeForAccountRef;
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
            }

            action = actions.get(msg.getClass());

            sessionCheck();

            policyCheck();

            msg.setSession(session);
        }

        void check(APIMessage msg) {
            this.msg = msg;
            if (msg.getClass().isAnnotationPresent(SuppressCredentialCheck.class)) {
                return;
            }

            DebugUtils.Assert(msg.getSession() != null, "session cannot be null");
            session = msg.getSession();

            action = actions.get(msg.getClass());
            policyCheck();
        }

        private void accountFieldCheck() throws IllegalAccessException {
            for (AccountCheckField af : action.accountCheckFields) {
                Object value = af.field.get(msg);
                if (value == null) {
                    continue;
                } else {
                    Set resourceUuids = new HashSet();

                    if (String.class.isAssignableFrom(af.field.getType())) {
                        resourceUuids.add(value);
                    } else if (Collection.class.isAssignableFrom(af.field.getType())) {
                        resourceUuids.addAll((Collection) value);

                    }

                    if (resourceUuids.isEmpty()) {
                        return;
                    }

                    List<Tuple> ts = SQL.New(" select uuid, accountUuid from :resourceType where uuid in (:resourceUuids) ", Tuple.class).param("resourceType", af.param.resourceType().getSimpleName()).param("resourceUuids", resourceUuids).list();
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
        }

        private void useDecision(Decision d, boolean userPolicy) {
            String policyCategory = userPolicy ? "user policy" : "group policy";

            if (d.effect == BillingConstant.StatementEffect.Allow) {
                logger.debug(String.format("API[name: %s, action: %s] is approved by a %s[name: %s, uuid: %s]," + " statement[action: %s]", msg.getClass().getSimpleName(), d.action, policyCategory, d.policy.getName(), d.policy.getUuid(), d.actionRule));
            } else {
                logger.debug(String.format("API[name: %s, action: %s] is denied by a %s[name: %s, uuid: %s]," + " statement[action: %s]", msg.getClass().getSimpleName(), d.action, policyCategory, d.policy.getName(), d.policy.getUuid(), d.actionRule));

                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("%s denied. user[name: %s, uuid: %s] is denied to execute API[%s]", policyCategory, username, session.getUuid(), msg.getClass().getSimpleName())));
            }
        }

        private void policyCheck() {
            if (session.isAdminAccountSession()) {
                return;
            }

            if (action.adminOnly) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("API[%s] is admin only", msg.getClass().getSimpleName())));
            }

            if (!session.isAdminUserSession()) {

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

            Map<String, String> headers = new HashMap<>();
            Map<String, Object> msgClass = new HashMap<>();
            Map<String, Object> param = new HashMap<>();
            param.put("uuid", msg.getSession().getUuid());
            msgClass.put(BillingConstant.GET_USER_POLICY_MSG, param);
            UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(BillingConstant.ACCOUNT_SERVER);
            String url = ub.build().toUriString();

            SessionInventory si = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(msgClass), headers, SessionInventory.class);
            List<PolicyInventory> userPolicys = null;
            Decision d = decide(userPolicys);
            if (d != null) {
                useDecision(d, true);
                return;
            }


            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.PERMISSION_DENIED, String.format("user[name: %s, uuid: %s] has no policy set for this operation, API[%s] is denied by default. You may either create policies for this user" + " or add the user into a group with polices set", username, session.getUserUuid(), msg.getClass().getSimpleName())));
        }

        class Decision {
            PolicyInventory policy;
            String action;
            PolicyInventory.PolicyStatement statement;
            String actionRule;
            BillingConstant.StatementEffect effect;
        }

        private Decision decide(List<PolicyInventory> userPolicys) {
            for (String a : action.actions) {
                for (PolicyInventory p : userPolicys) {
                    for (PolicyInventory.PolicyStatement s : p.getStatements()) {
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
                                logger.trace(String.format("API[name: %s, action: %s] is not matched by policy[name: %s, uuid: %s" + ", statement[action: %s, effect: %s]", msg.getClass().getSimpleName(), a, p.getName(), p.getUuid(), ac, s.getEffect()));
                            }
                        }
                    }
                }
            }

            return null;
        }

        private void sessionCheck() {
            if (msg.getSession() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, String.format("session of message[%s] is null", msg.getMessageName())));
            }

            if (msg.getSession().getUuid() == null) {
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "session uuid is null"));
            }

            SessionInventory session = sessions.get(msg.getSession().getUuid());

            if (session == null) {
                Map<String, String> headers = new HashMap<>();
                Map<String, Object> msgClass = new HashMap<>();
                Map<String, Object> param = new HashMap<>();
                param.put("uuid", msg.getSession().getUuid());
                msgClass.put(BillingConstant.GET_SESSION_VALID_MSG, param);
                UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(BillingConstant.ACCOUNT_SERVER);
                String url = ub.build().toUriString();
                session = restf.syncJsonPost(url, JSONObjectUtil.toJsonString(msgClass), headers, SessionInventory.class);

                if (session == null) {
                    throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
                }

                sessions.put(session.getUuid(), session);
            }

            Timestamp curr = new Timestamp(System.currentTimeMillis());
            if (curr.after(session.getExpiredDate())) {
                logger.debug(String.format("session expired[%s < %s] for account[uuid:%s]", curr, session.getExpiredDate(), session.getAccountUuid()));
                sessions.remove(session.getUuid());
                throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
            }

            this.session = session;
        }
    }

}
