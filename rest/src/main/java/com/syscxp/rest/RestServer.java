package com.syscxp.rest;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusEventListener;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.retry.Retry;
import com.syscxp.core.retry.RetryCondition;
import com.syscxp.header.Component;
import com.syscxp.header.Constants;
import com.syscxp.header.MapField;
import com.syscxp.header.apimediator.ApiMediatorConstant;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.*;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.query.QueryCondition;
import com.syscxp.header.query.QueryOp;
import com.syscxp.header.rest.*;
import com.syscxp.rest.sdk.DocumentGenerator;
import com.syscxp.rest.sdk.SdkFile;
import com.syscxp.rest.sdk.SdkTemplate;
import com.syscxp.utils.*;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import okhttp3.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 14:20
 * Author: wj
 */
public class RestServer implements Component, CloudBusEventListener {
    private static final CLogger logger = Utils.getLogger(RestServer.class);
    private static final Logger requestLogger = LogManager.getLogger("api.request");
    private static ThreadLocal<RequestInfo> requestInfo = new ThreadLocal<>();

    private static final OkHttpClient http = new OkHttpClient();
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    private CloudBus bus;
    @Autowired
    private AsyncRestApiStore asyncStore;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private PluginRegistry pluginRgty;

    private List<RestServletRequestInterceptor> interceptors = new ArrayList<>();

    public void registerRestServletRequestInterceptor(RestServletRequestInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    static class RequestInfo {
        // don't save session to database as JSON
        // it's not JSON-dumpable
        transient HttpSession session;
        String remoteHost;
        String requestUrl;
        HttpHeaders headers = new HttpHeaders();

        public RequestInfo(HttpServletRequest req) {
            session = req.getSession();
            remoteHost = req.getRemoteHost();

            for (Enumeration e = req.getHeaderNames(); e.hasMoreElements(); ) {
                String name = e.nextElement().toString();
                headers.add(name, req.getHeader(name));
            }

            try {
                requestUrl = URLDecoder.decode(req.getRequestURI(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new CloudRuntimeException(e);
            }
        }
    }


    public static void generateJavaSdk() {
        String path = PathUtil.join(System.getProperty("user.home"), "syscxp-sdk/java");
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            Class clz = GroovyUtils.getClass("scripts/SdkApiTemplate.groovy", RestServer.class.getClassLoader());
            Set<Class<?>> apiClasses = Platform.getReflections().getTypesAnnotatedWith(RestRequest.class)
                    .stream().filter(it -> it.isAnnotationPresent(RestRequest.class)).collect(Collectors.toSet());

            List<SdkFile> allFiles = new ArrayList<>();
            for (Class apiClz : apiClasses) {
                if (Modifier.isAbstract(apiClz.getModifiers())) {
                    continue;
                }

                SdkTemplate tmp = (SdkTemplate) clz.getConstructor(Class.class).newInstance(apiClz);
                allFiles.addAll(tmp.generate());
            }

            SdkTemplate tmp = GroovyUtils.newInstance("scripts/SdkDataStructureGenerator.groovy", RestServer.class.getClassLoader());
            allFiles.addAll(tmp.generate());

            for (SdkFile f : allFiles) {
                //logger.debug(String.format("\n%s", f.getContent()));
                String fpath = PathUtil.join(path, f.getFileName());
                FileUtils.writeStringToFile(new File(fpath), f.getContent());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new CloudRuntimeException(e);
        }
    }

    @Override
    public boolean handleEvent(Event e) {
        if (e instanceof APIEvent) {
            RequestData d = asyncStore.complete((APIEvent) e);

            if (d != null && d.webHook != null) {
                try {
                    callWebHook(d);
                } catch (Throwable t) {
                    throw new CloudRuntimeException(t);
                }
            }
        }

        return false;
    }

    static class WebHookRetryException extends RuntimeException {
        public WebHookRetryException() {
        }

        public WebHookRetryException(String message) {
            super(message);
        }

        public WebHookRetryException(String message, Throwable cause) {
            super(message, cause);
        }

        public WebHookRetryException(Throwable cause) {
            super(cause);
        }

        public WebHookRetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    private void callWebHook(RequestData d) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        requestInfo.set(d.requestInfo);

        AsyncRestQueryResult ret = asyncStore.query(d.apiMessage.getId());

        ApiResponse response = new ApiResponse();
        // task is done
        APIEvent evt = ret.getResult();
        if (evt.isSuccess()) {
            RestResponseWrapper w = responseAnnotationByClass.get(evt.getClass());
            if (w == null) {
                throw new CloudRuntimeException(String.format("cannot find RestResponseWrapper for the class[%s]", evt.getClass()));
            }

            writeResponse(response, w, ret.getResult());
        } else {
            response.setError(evt.getError());
        }

        String body = JSONObjectUtil.toJsonString(response);
        HttpUrl url = HttpUrl.parse(d.webHook);
        Request.Builder rb = new Request.Builder().url(url)
                .post(RequestBody.create(JSON, body))
                .addHeader(RestConstants.HEADER_JOB_UUID, d.apiMessage.getId())
                .addHeader(RestConstants.HEADER_JOB_SUCCESS, String.valueOf(evt.isSuccess()));

        Request request = rb.build();

        new Retry<Void>() {
            String __name__ = String.format("call-webhook-%s", d.webHook);

            @Override
            @RetryCondition(onExceptions = {WebHookRetryException.class}, times = 15, interval = 2)
            protected Void call() {
                try {
                    if (requestLogger.isTraceEnabled()) {
                        StringBuilder sb = new StringBuilder(String.format("Call Web-Hook[%s] (to %s%s)", d.webHook, d.requestInfo.remoteHost, d.requestInfo.requestUrl));
                        sb.append(String.format(" Body: %s", body));

                        requestLogger.trace(sb.toString());
                    }

                    try (Response r = http.newCall(request).execute()) {
                        if (r.code() < 200 || r.code() >= 300) {
                            throw new WebHookRetryException(String.format("failed to post to the webhook[%s], %s",
                                    d.webHook, r.toString()));
                        }
                    }

                } catch (IOException e) {
                    throw new WebHookRetryException(e);
                }

                return null;
            }
        }.run();
    }

    class Api {
        Class apiClass;
        Class apiResponseClass;
        RestRequest requestAnnotation;
        RestResponse responseAnnotation;
        Map<String, String> requestMappingFields;
        String path;
        String action;
        List<String> optionalActions;

        Map<String, Field> allApiClassFields = new HashMap<>();

        @Override
        public String toString() {
            return String.format("%s-%s-%s", requestAnnotation.method(), path, action);
        }

        Api(Class clz, RestRequest at) {
            apiClass = clz;
            requestAnnotation = at;
            apiResponseClass = at.responseClass();
            path = String.format("%s/%s/%s", RestGlobalConfig.SYSCXP_API_SERVER_URL.value(), restf.getPath(), RestConstants.API_VERSION);

            if (at.mappingFields().length > 0) {
                requestMappingFields = new HashMap<>();

                for (String mf : at.mappingFields()) {
                    String[] kv = mf.split("=");
                    if (kv.length != 2) {
                        throw new CloudRuntimeException(String.format("bad requestMappingField[%s] of %s", mf, apiClass));
                    }

                    requestMappingFields.put(kv[0].trim(), kv[1].trim());
                }
            }

            responseAnnotation = (RestResponse) apiResponseClass.getAnnotation(RestResponse.class);
            DebugUtils.Assert(responseAnnotation != null, String.format("%s must be annotated with @RestResponse", apiResponseClass));

            Collections.addAll(optionalActions, at.optionalActions());

            if (!at.isAction() && requestAnnotation.action().isEmpty()) {
                throw new CloudRuntimeException(String.format("Invalid @RestRequest of %s, either isAction must be set to true or" +
                        " action is set to a non-empty string", apiClass.getName()));
            }

            if (at.isAction()) {
                action = StringUtils.removeStart(apiClass.getSimpleName(), "API");
                action = StringUtils.removeEnd(action, "Msg");
//                action = StringUtils.uncapitalize(action);
            } else {
                action = requestAnnotation.action();
            }

            List<Field> fs = FieldUtils.getAllFields(apiClass);
            fs = fs.stream().filter(f -> !f.isAnnotationPresent(APINoSee.class) && !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
            for (Field f : fs) {
                allApiClassFields.put(f.getName(), f);

                if (requestAnnotation.method() == HttpMethod.GET) {
                    if (APIQueryMessage.class.isAssignableFrom(apiClass)) {
                        // query messages are specially handled
                        continue;
                    }

                    if (Collection.class.isAssignableFrom(f.getType())) {
                        Class gtype = FieldUtils.getGenericType(f);

                        if (gtype == null) {
                            throw new CloudRuntimeException(String.format("%s.%s is of collection type but doesn't not have" +
                                    " a generic type", apiClass, f.getName()));
                        }

                        if (!gtype.getName().startsWith("java.")) {
                            throw new CloudRuntimeException(String.format("%s.%s is of collection type with a generic type" +
                                    "[%s] not belonging to JDK", apiClass, f.getName(), gtype));
                        }
                    } else if (Map.class.isAssignableFrom(f.getType())) {
                        throw new CloudRuntimeException(String.format("%s.%s is of map type, however, the GET method doesn't" +
                                " support query parameters of map type", apiClass, f.getName()));
                    }
                }
            }
        }

        private void mapQueryParameterToApiFieldValue(String name, String[] vals, Map<String, Object> params) throws RestException {
            String[] pairs = name.split("\\.");
            String fname = pairs[0];
            String key = pairs[1];

            Field f = allApiClassFields.get(fname);
            if (f == null) {
                logger.warn(String.format("unknown map query parameter[%s], ignore", name));
                return;
            }

            MapField at = f.getAnnotation(MapField.class);
            DebugUtils.Assert(at != null, String.format("%s::%s must be annotated by @MapField", apiClass, fname));

            Map m = (Map) params.get(fname);
            if (m == null) {
                m = new HashMap();
                params.put(fname, m);
            }

            if (m.containsKey(key)) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(),
                        String.format("duplicate map query parameter[%s], there has been a parameter with the same map key", name));
            }

            if (Collection.class.isAssignableFrom(at.valueType())) {
                m.put(key, asList(vals));
            } else {
                if (vals.length > 1) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(),
                            String.format("Invalid query parameter[%s], only one value is allowed for the parameter but" +
                                    " multiple values found", name));
                }

                m.put(key, vals[0]);
            }
        }

        Object queryParameterToApiFieldValue(String name, String[] vals) throws RestException {
            Field f = allApiClassFields.get(name);
            if (f == null) {
                return null;
            }

            if (Collection.class.isAssignableFrom(f.getType())) {
                Class gtype = FieldUtils.getGenericType(f);
                List lst = new ArrayList();
                for (String v : vals) {
                    lst.add(TypeUtils.stringToValue(v, gtype));
                }

                return lst;
            } else {
                if (vals.length > 1) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(),
                            String.format("Invalid query parameter[%s], only one value is allowed for the parameter but" +
                                    " multiple values found", name));
                }

                return TypeUtils.stringToValue(vals[0], f.getType());
            }
        }
    }

    class RestException extends Exception {
        private int statusCode;
        private String error;

        public RestException(int statusCode, String error) {
            this.statusCode = statusCode;
            this.error = error;
        }
    }

    class RestResponseWrapper {
        RestResponse annotation;
        Map<String, String> responseMappingFields = new HashMap<>();
        Class apiResponseClass;

        public RestResponseWrapper(RestResponse annotation, Class apiResponseClass) {
            this.annotation = annotation;
            this.apiResponseClass = apiResponseClass;

            if (annotation.fieldsTo().length > 0) {
                responseMappingFields = new HashMap<>();

                if (annotation.superclassFieldsTo().length == 1 && "all".equals(annotation.superclassFieldsTo()[0])) {
                    Field[] fields = apiResponseClass.getDeclaredFields();

                    List<Field> apiFields = Arrays.stream(fields).filter(f -> !f.isAnnotationPresent(APINoSee.class)
                            && !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());

                    for (Field f : apiFields) {
                        responseMappingFields.put(f.getName(), f.getName());
                    }
                } else {
                    for (String mf : annotation.fieldsTo()) {
                        String[] kv = mf.split("=");
                        if (kv.length == 2) {
                            responseMappingFields.put(kv[0].trim(), kv[1].trim());
                        } else if (kv.length == 1) {
                            responseMappingFields.put(kv[0].trim(), kv[0].trim());
                        } else {
                            throw new CloudRuntimeException(String.format("bad mappingFields[%s] of %s", mf, apiResponseClass));
                        }

                    }
                }

                if (annotation.fieldsTo().length == 1 && "all".equals(annotation.fieldsTo()[0])) {
                    Field[] fields = apiResponseClass.getDeclaredFields();

                   List<Field> apiFields = Arrays.stream(fields).filter(f -> !f.isAnnotationPresent(APINoSee.class)
                            && !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());

                    for (Field f : apiFields) {
                        responseMappingFields.put(f.getName(), f.getName());
                    }
                } else {
                    for (String mf : annotation.fieldsTo()) {
                        String[] kv = mf.split("=");
                        if (kv.length == 2) {
                            responseMappingFields.put(kv[0].trim(), kv[1].trim());
                        } else if (kv.length == 1) {
                            responseMappingFields.put(kv[0].trim(), kv[0].trim());
                        } else {
                            throw new CloudRuntimeException(String.format("bad mappingFields[%s] of %s", mf, apiResponseClass));
                        }

                    }
                }
            }
        }
    }

    void init() throws IllegalAccessException, InstantiationException {
        bus.subscribeEvent(this, new APIEvent());
    }

    private AntPathMatcher matcher = new AntPathMatcher();

    private Map<String, Api> apis = new HashMap<>();
    private Map<Class, RestResponseWrapper> responseAnnotationByClass = new HashMap<>();

    private HttpEntity<String> toHttpEntity(HttpServletRequest req) {
        try {
            String body = IOUtils.toString(req.getReader());
            req.getReader().close();

            HttpHeaders header = new HttpHeaders();
            for (Enumeration e = req.getHeaderNames(); e.hasMoreElements(); ) {
                String name = e.nextElement().toString();
                header.add(name, req.getHeader(name));
            }

            return new HttpEntity<>(body, header);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new CloudRuntimeException(e);
        }
    }

    private void sendResponse(int statusCode, String body, HttpServletResponse rsp) throws IOException {
        if (requestLogger.isTraceEnabled()) {
            RequestInfo info = requestInfo.get();

            StringBuilder sb = new StringBuilder(String.format("[ID: %s] Response to %s (%s),", info.session.getId(),
                    info.remoteHost, info.requestUrl));
            sb.append(String.format(" Status Code: %s,", statusCode));
            sb.append(String.format(" Body: %s", body == null || body.isEmpty() ? null : body));

            requestLogger.trace(sb.toString());
        }

        rsp.setStatus(statusCode);
        rsp.getWriter().write(body == null ? "" : body);
    }


    void handle(HttpServletRequest req, HttpServletResponse rsp) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        requestInfo.set(new RequestInfo(req));
        rsp.setCharacterEncoding("utf-8");
        String action = req.getParameter(RestConstants.ACTION);
        HttpEntity<String> entity = toHttpEntity(req);

        if (requestLogger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder(String.format("[ID: %s, Method: %s] Request from %s (to %s), ",
                    req.getSession().getId(), req.getMethod(),
                    req.getRemoteHost(), URLDecoder.decode(req.getRequestURI(), "UTF-8")));
            sb.append(String.format(" Headers: %s,", JSONObjectUtil.toJsonString(entity.getHeaders())));
            if (req.getQueryString() != null && !req.getQueryString().isEmpty()) {
                sb.append(String.format(" Query: %s,", URLDecoder.decode(req.getQueryString(), "UTF-8")));
            }
            sb.append(String.format(" Body: %s", entity.getBody().isEmpty() ? null : entity.getBody()));

            requestLogger.trace(sb.toString());
        }

        try {
            for (RestServletRequestInterceptor ic : interceptors) {
                ic.intercept(req);
            }
        } catch (RestServletRequestInterceptor.RestServletRequestInterceptorException e) {
            sendResponse(e.statusCode, e.error, rsp);
            return;
        }

        if (RestConstants.ASYNC_JOB_ACTION.equals(action)) {
            handleJobQuery(req, rsp);
            return;
        }

        Api api = apis.get(action);

        if (api == null) {
            sendResponse(HttpStatus.NOT_FOUND.value(), String.format("no api mapping to Action[name: %s]", action), rsp);
            return;
        }

        if (HttpMethod.valueOf(req.getMethod()) != api.requestAnnotation.method()) {
            sendResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), String.format("The method[%s] is not allowed for" +
                    " the path[%s]", req.getMethod(), req.getRequestURI()), rsp);
            return;
        }

        try {
            handleApi(api, req, rsp);
        } catch (RestException e) {
            sendResponse(e.statusCode, e.error, rsp);
        } catch (Throwable e) {
            logger.warn(String.format("failed to handle API to Action[name: %s]", action), e);
            sendResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), rsp);
        }
    }

    private void handleJobQuery(HttpServletRequest req, HttpServletResponse rsp) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!req.getMethod().equals(HttpMethod.GET.name())) {
            sendResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "only GET method is allowed for querying job status", rsp);
            return;
        }

        String uuid = req.getParameter("uuid");
        AsyncRestQueryResult ret = asyncStore.query(uuid);

        if (ret.getState() == AsyncRestState.expired) {
            sendResponse(HttpStatus.NOT_FOUND.value(), "the job has been expired", rsp);
            return;
        }

        ApiResponse response = new ApiResponse();

        if (ret.getState() == AsyncRestState.processing) {
            sendResponse(HttpStatus.ACCEPTED.value(), response, rsp);
            return;
        }

        // task is done
        APIEvent evt = ret.getResult();
        if (evt.isSuccess()) {
            RestResponseWrapper w = responseAnnotationByClass.get(evt.getClass());
            if (w == null) {
                throw new CloudRuntimeException(String.format("cannot find RestResponseWrapper for the class[%s]", evt.getClass()));
            }
            writeResponse(response, w, ret.getResult());
            sendResponse(HttpStatus.OK.value(), response, rsp);
        } else {
            response.setError(evt.getError());
            sendResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), response, rsp);
        }
    }

    private void sendResponse(int statusCode, ApiResponse response, HttpServletResponse rsp) throws IOException {
        sendResponse(statusCode, response.isEmpty() ? "" : JSONObjectUtil.toJsonString(response), rsp);
    }

    private String getSession(HttpServletRequest req) {
//        return (String) req.getAttribute(RestConstants.SESSION_UUID);
        return (String) req.getAttribute(RestConstants.SESSION_UUID);
    }

    private void handleApi(Api api, HttpServletRequest req, HttpServletResponse rsp) throws RestException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, IOException {

        String sessionId = null;
        if (!api.apiClass.isAnnotationPresent(SuppressCredentialCheck.class)) {
            sessionId = getSession(req);
        }
        Map<String, String[]> queryParameters = req.getParameterMap();

        if (APIQueryMessage.class.isAssignableFrom(api.apiClass)) {
            handleQueryApi(api, sessionId, req, rsp);
            return;
        }
        List<String> params = asList(RestConstants.ACTION, RestConstants.SECRET_ID, RestConstants.SIGNATURE,
                RestConstants.SIGNATURE_METHOD, RestConstants.NONCE, RestConstants.TIMESTAMP);
        // uses query string to pass parameters
        Map<String, Object> parameter = new HashMap<>();
        for (Map.Entry<String, String[]> e : queryParameters.entrySet()) {
            String k = e.getKey();
            String[] vals = e.getValue();

            if (params.contains(k)) {
                continue;
            }
            if (k.contains(".")) {
                // this is a map parameter
                api.mapQueryParameterToApiFieldValue(k, vals, parameter);
            } else {
                Object val = api.queryParameterToApiFieldValue(k, vals);
                if (val == null) {
                    logger.warn(String.format("unknown query parameter[%s], ignored", k));
                    continue;
                }
                parameter.put(k, val);
            }
        }

        APIMessage msg;
        if (!parameter.isEmpty()) {
            msg = (APIMessage) api.apiClass.newInstance();
        } else {
            // check boolean type parameters
            for (Field f : api.apiClass.getDeclaredFields()) {
                if (f.getType().isAssignableFrom(boolean.class)) {
                    Object booleanObject = parameter.get(f.getName());
                    if (booleanObject == null) {
                        continue;
                    }
                    String booleanValue = booleanObject.toString();
                    if (!(booleanValue.equalsIgnoreCase("true") ||
                            booleanValue.equalsIgnoreCase("false"))) {
                        throw new RestException(HttpStatus.BAD_REQUEST.value(),
                                String.format("Invalid value for boolean field [%s]," +
                                                " [%s] is not a valid boolean string[true, false].",
                                        f.getName(), booleanValue));
                    }
                }
            }

            msg = JSONObjectUtil.rehashObject(parameter, (Class<APIMessage>) api.apiClass);
        }

        if (requestInfo.get().headers.containsKey(RestConstants.HEADER_JOB_UUID)) {
            String jobUuid = requestInfo.get().headers.get(RestConstants.HEADER_JOB_UUID).get(0);
            if (jobUuid.length() != 32) {
                throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format("Invalid header[%s], it" +
                        " must be a UUID with '-' stripped", RestConstants.HEADER_JOB_UUID));
            }

            msg.setId(jobUuid);
        }

        if (sessionId != null) {
            SessionInventory session = new SessionInventory();
            session.setUuid(sessionId);
            msg.setSession(session);
        }

        for (Map.Entry<String, Object> e : parameter.entrySet()) {

            Class clazz = PropertyUtils.getPropertyType(msg, e.getKey());
            if (clazz.isEnum())
                PropertyUtils.setProperty(msg, e.getKey(), Enum.valueOf(clazz, String.valueOf(e.getValue())));
            else {
                PropertyUtils.setProperty(msg, e.getKey(), e.getValue());
            }
        }

        msg.setServiceId(ApiMediatorConstant.SERVICE_ID);
        sendMessage(msg, api, rsp);
    }

    private static final LinkedHashMap<String, String> QUERY_OP_MAPPING = new LinkedHashMap();

    static {
        // DO NOT change the order
        // an operator contained by another operator must be placed
        // after the containing operator. For example, "=" is contained
        // by "!=" so it must sit after "!="

        QUERY_OP_MAPPING.put("!=", QueryOp.NOT_EQ.toString());
        QUERY_OP_MAPPING.put(">=", QueryOp.GT_AND_EQ.toString());
        QUERY_OP_MAPPING.put("<=", QueryOp.LT_AND_EQ.toString());
        QUERY_OP_MAPPING.put("!?=", QueryOp.NOT_IN.toString());
        QUERY_OP_MAPPING.put("!~=", QueryOp.NOT_LIKE.toString());
        QUERY_OP_MAPPING.put("~=", QueryOp.LIKE.toString());
        QUERY_OP_MAPPING.put("?=", QueryOp.IN.toString());
        QUERY_OP_MAPPING.put("=", QueryOp.EQ.toString());
        QUERY_OP_MAPPING.put(">", QueryOp.GT.toString());
        QUERY_OP_MAPPING.put("<", QueryOp.LT.toString());
        QUERY_OP_MAPPING.put("is null", QueryOp.IS_NULL.toString());
        QUERY_OP_MAPPING.put("not null", QueryOp.NOT_NULL.toString());
    }

    private void handleQueryApi(Api api, String sessionId, HttpServletRequest req, HttpServletResponse rsp) throws IllegalAccessException, InstantiationException, RestException, IOException, NoSuchMethodException, InvocationTargetException {
        Map<String, String[]> vars = req.getParameterMap();
        APIQueryMessage msg = (APIQueryMessage) api.apiClass.newInstance();

        SessionInventory session = new SessionInventory();
        session.setUuid(sessionId);
        msg.setSession(session);
        msg.setServiceId(ApiMediatorConstant.SERVICE_ID);

        String uuid = req.getParameter("uuid");
        if (uuid != null) {
            // return the resource directly
            QueryCondition qc = new QueryCondition();
            qc.setName("uuid");
            qc.setOp("=");
            qc.setValue(uuid);
            msg.getConditions().add(qc);

            sendMessage(msg, api, rsp);
            return;
        }

        // a query with conditions
        for (Map.Entry<String, String[]> e : vars.entrySet()) {
            String varname = e.getKey().trim();
            String varvalue = e.getValue()[0].trim();

            if ("limit".equals(varname)) {
                try {
                    msg.setLimit(Integer.valueOf(varvalue));
                } catch (NumberFormatException ex) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(), "Invalid query parameter. 'limit' must be an integer");
                }
            } else if ("start".equals(varname)) {
                try {
                    msg.setStart(Integer.valueOf(varvalue));
                } catch (NumberFormatException ex) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(), "Invalid query parameter. 'start' must be an integer");
                }
            } else if ("count".equals(varname)) {
                msg.setCount(Boolean.valueOf(varvalue));
            } else if ("groupBy".equals(varname)) {
                msg.setGroupBy(varvalue);
            } else if ("replyWithCount".equals(varname)) {
                msg.setReplyWithCount(Boolean.valueOf(varvalue));
            } else if ("sort".equals(varname)) {
                if (varvalue.startsWith("+")) {
                    msg.setSortDirection("asc");
                    varvalue = StringUtils.stripStart(varvalue, "+");
                } else if (varvalue.startsWith("-")) {
                    msg.setSortDirection("desc");
                    varvalue = StringUtils.stripStart(varvalue, "-");
                } else {
                    msg.setSortDirection("asc");
                }

                msg.setSortBy(varvalue);
            } else if ("q".startsWith(varname)) {
                String[] conds = e.getValue();

                for (String cond : conds) {
                    String OP = null;
                    String delimiter = null;
                    for (String op : QUERY_OP_MAPPING.keySet()) {
                        if (cond.contains(op)) {
                            OP = QUERY_OP_MAPPING.get(op);
                            delimiter = op;
                            break;
                        }
                    }

                    if (OP == null) {
                        throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format("Invalid query parameter." +
                                " The '%s' in the parameter[q] doesn't contain any query operator. Valid query operators are" +
                                " %s", cond, asList(QUERY_OP_MAPPING.keySet())));
                    }

                    QueryCondition qc = new QueryCondition();
                    String[] ks = StringUtils.splitByWholeSeparator(cond, delimiter, 2);
                    if (OP.equals(QueryOp.IS_NULL.toString()) || OP.equals(QueryOp.NOT_NULL.toString())) {
                        String cname = ks[0].trim();
                        qc.setName(cname);
                        qc.setOp(OP);
                    } else {
                        if (ks.length != 2) {
                            throw new RestException(HttpStatus.BAD_REQUEST.value(), String.format("Invalid query parameter." +
                                    " The '%s' in parameter[q] is not a key-value pair split by %s", cond, OP));
                        }

                        String cname = ks[0].trim();
                        String cvalue = ks[1]; // don't trim the value, a space is valid in some conditions
                        qc.setName(cname);
                        qc.setOp(OP);
                        qc.setValue(cvalue);
                    }

                    msg.getConditions().add(qc);
                }
            } else if ("fields".equals(varname)) {
                List<String> fs = new ArrayList<>();
                for (String f : varvalue.split(",")) {
                    fs.add(f.trim());
                }

                if (fs.isEmpty()) {
                    throw new RestException(HttpStatus.BAD_REQUEST.value(), "Invalid query parameter. 'fields'" +
                            " contains zero field");
                }
                msg.setFields(fs);
            }
        }

        if (msg.getConditions() == null) {
            msg.setConditions(new ArrayList<>());
        }

        sendMessage(msg, api, rsp);
    }

    private void writeResponse(ApiResponse response, RestResponseWrapper w, Object replyOrEvent) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!w.annotation.allTo().equals("")) {
            response.put(w.annotation.allTo(),
                    PropertyUtils.getProperty(replyOrEvent, w.annotation.allTo()));
        } else {
            for (Map.Entry<String, String> e : w.responseMappingFields.entrySet()) {
                response.put(e.getKey(),
                        PropertyUtils.getProperty(replyOrEvent, e.getValue()));
            }
        }

        // TODO: fix hard code hack
        if (APIQueryReply.class.isAssignableFrom(w.apiResponseClass)) {
            Object total = PropertyUtils.getProperty(replyOrEvent, "total");
            if (total != null) {
                response.put("total", total);
            }
        }

        if (requestInfo.get().headers.containsKey(RestConstants.HEADER_JSON_SCHEMA)
                // set schema anyway if it's a query API
                || APIQueryReply.class.isAssignableFrom(w.apiResponseClass)) {
            response.setSchema(new JsonSchemaBuilder(response).build());
        }
    }

    private void sendReplyResponse(MessageReply reply, Api api, HttpServletResponse rsp) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ApiResponse response = new ApiResponse();

        if (!reply.isSuccess()) {
            response.setError(reply.getError());
            sendResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), JSONObjectUtil.toJsonString(response), rsp);
            return;
        }

        // the api succeeded

        writeResponse(response, responseAnnotationByClass.get(api.apiResponseClass), reply);
        sendResponse(HttpStatus.OK.value(), response, rsp);
    }

    private void sendMessage(APIMessage msg, Api api, HttpServletResponse rsp) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (msg instanceof APISyncCallMessage) {
            MessageReply reply = bus.call(msg);
            sendReplyResponse(reply, api, rsp);
        } else {
            RequestData d = new RequestData();
            d.apiMessage = msg;
            d.requestInfo = requestInfo.get();
            List<String> webHook = requestInfo.get().headers.get(RestConstants.HEADER_WEBHOOK);
            if (webHook != null && !webHook.isEmpty()) {
                d.webHook = webHook.get(0);
            }

            asyncStore.save(d);

            ApiResponse response = new ApiResponse();
            response.setResult(msg.getId());

            bus.send(msg);

            sendResponse(HttpStatus.ACCEPTED.value(), response, rsp);
        }
    }

    @Override
    public boolean start() {
        build();
        populateExtensions();
        RestGlobalConfig.SYSCXP_API_SERVER_URL.installUpdateExtension((oldConfig, newConfig) -> {
            logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                    oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
            build();
        });
        return true;
    }

    private void populateExtensions() {
        for (RestServletRequestInterceptor ri : pluginRgty.getExtensionList(RestServletRequestInterceptor.class)) {
            registerRestServletRequestInterceptor(ri);
        }
    }

    private void collectRestRequestErrConfigApi(List<String> errorApiList, Class apiClass, RestRequest apiRestRequest) {
        if (apiRestRequest.isAction() && !RESTConstant.EMPTY_STRING.equals(apiRestRequest.action())) {
            errorApiList.add(String.format("[%s] RestRequest config error, Setting action is not allowed " +
                    "when isAction set true", apiClass.getName()));
        }
    }

    private void build() {
        Reflections reflections = Platform.getReflections();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RestRequest.class).stream()
                .filter(it -> it.isAnnotationPresent(RestRequest.class)).collect(Collectors.toSet());

        List<String> errorApiList = new ArrayList<>();
        for (Class clz : classes) {
            RestRequest at = (RestRequest) clz.getAnnotation(RestRequest.class);

            collectRestRequestErrConfigApi(errorApiList, clz, at);

            Api api = new Api(clz, at);

            List<String> actions = new ArrayList<>();
            if (!"null".equals(api.action)) {
                actions.add(api.action);
            }
            Collections.addAll(actions, at.optionalActions());

            for (String action : actions) {
                api = new Api(clz, at);
                api.action = action;

                Api old = apis.get(action);

                if (old != null) {
                    throw new CloudRuntimeException(String.format("duplicate rest API[%s, %s], they are both actions with the" +
                            " same action name[%s]", clz, old.apiClass, action));
                }
                apis.put(action, api);
            }


            responseAnnotationByClass.put(api.apiResponseClass, new RestResponseWrapper(api.responseAnnotation, api.apiResponseClass));
        }

        responseAnnotationByClass.put(APIEvent.class, new RestResponseWrapper(new RestResponse() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String allTo() {
                return "";
            }

            @Override
            public String[] fieldsTo() {
                return new String[0];
            }

            @Override
            public String[] superclassFieldsTo() {
                return new String[0];
            }

        }, APIEvent.class));

        if (errorApiList.size() > 0) {
            logger.error(String.format("Error Api list : %s", errorApiList));
            throw new RuntimeException(String.format("Error Api list : %s", errorApiList));
        }

    }

    @Override
    public boolean stop() {
        return true;
    }
}
