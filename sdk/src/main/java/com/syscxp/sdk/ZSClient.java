package com.syscxp.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:06
 * Author: wj
 */
public class ZSClient {
    private static OkHttpClient http = new OkHttpClient();

    static final Gson gson;
    static final Gson prettyGson;

    private static ConcurrentHashMap<String, Api> waittingApis = new ConcurrentHashMap<>();

    private static final long ACTION_DEFAULT_TIMEOUT = -1;
    private static final long ACTION_DEFAULT_POLLINGINTERVAL = -1;

    static {
        gson = new GsonBuilder().create();
        prettyGson = new GsonBuilder().setPrettyPrinting().create();
    }

    private static ZSConfig config;

    public static ZSConfig getConfig() {
        return config;
    }

    public static void configure(ZSConfig c) {
        config = c;

        if (c.readTimeout != null || c.writeTimeout != null) {
            OkHttpClient.Builder b = new OkHttpClient.Builder();

            if (c.readTimeout != null) {
                b.readTimeout(c.readTimeout, TimeUnit.MILLISECONDS);
            }
            if (c.writeTimeout != null) {
                b.writeTimeout(c.writeTimeout, TimeUnit.MILLISECONDS);
            }

            http = b.build();
        }
    }

    public static void webHookCallback(HttpServletRequest req, HttpServletResponse rsp) {
        try {
            StringBuilder jb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }

            String jobUuid = req.getHeader(Constants.HEADER_JOB_UUID);
            if (jobUuid == null) {
                // TODO: log error
                rsp.sendError(400, String.format("missing header[%s]", Constants.HEADER_JOB_UUID));
                return;
            }

            String jobSuccess = req.getHeader(Constants.HEADER_JOB_SUCCESS);
            if (jobSuccess == null) {
                // TODO: log error
                rsp.sendError(400, String.format("missing header[%s]", Constants.HEADER_JOB_SUCCESS));
                return;
            }

            boolean success = Boolean.valueOf(jobSuccess);

            ApiResult res = new ApiResult();
            if (!success) {
                res = gson.fromJson(jb.toString(), ApiResult.class);
            } else {
                res.setResultString(jb.toString());
            }

            Api api = waittingApis.get(jobUuid);
            if (api == null) {
                //TODO: log error
                rsp.sendError(404, String.format("no job[uuid:%s] found", jobUuid));
                return;
            }

            api.wakeUpFromWebHook(res);
            rsp.setStatus(200);
            rsp.getWriter().write("");
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }


    static String join(Collection lst, String sep) {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (Object s : lst) {
            if (first) {
                ret = new StringBuilder(s.toString());
                first = false;
                continue;
            }

            ret.append(sep).append(s.toString());
        }

        return ret.toString();
    }

    static class Api {
        AbstractAction action;
        RestInfo info;
        InternalCompletion completion;
        String jobUuid = UUID.randomUUID().toString().replaceAll("-", "");

        private ApiResult resultFromWebHook;

        Api(AbstractAction action) {
            this.action = action;
            info = action.getRestInfo();
            if (action.apiId != null) {
                jobUuid = action.apiId;
            }
        }

        void wakeUpFromWebHook(ApiResult res) {
            if (completion == null) {
                resultFromWebHook = res;
                synchronized (this) {
                    this.notifyAll();
                }
            } else {
                try {
                    completion.complete(res);
                } catch (Throwable t) {
                    res = new ApiResult();
                    res.code = Constants.INTERNAL_ERROR;
                    res.message = t.getMessage();
                    completion.complete(res);
                }
            }
        }

        void call(InternalCompletion completion) {
            this.completion = completion;
            doCall();
        }

        ApiResult doCall() {
            action.checkParameters();

            Request.Builder reqBuilder = new Request.Builder()
                    .addHeader(Constants.HEADER_JOB_UUID, jobUuid)
                    .addHeader(Constants.HEADER_JSON_SCHEMA, Boolean.TRUE.toString());

            if (config.webHook != null) {
                reqBuilder.addHeader(Constants.HEADER_WEBHOOK, config.webHook);
            }

            if (action instanceof QueryAction) {
                fillQueryApiRequestBuilder(reqBuilder);
            } else {
                fillNonQueryApiRequestBuilder(reqBuilder);
            }

            Request request = reqBuilder.build();

            try {
                if (config.webHook != null) {
                    waittingApis.put(jobUuid, this);
                }

                try (Response response = http.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        return httpError(response.code(), response.body().string());
                    }

                    if (response.code() == 200 || response.code() == 204) {
                        return writeApiResult(response);
                    } else if (response.code() == 202) {

                        if (config.webHook != null) {
                            return webHookResult();
                        } else {
                            return pollResult(response);
                        }
                    } else {
                        throw new ApiException(String.format("[Internal Error] the server returns an unknown status code[%s]", response.code()));
                    }
                }
            } catch (IOException e) {
                throw new ApiException(e);
            }
        }

        private ApiResult syncWebHookResult() {
            synchronized (this) {
                Long timeout = this.getTimeout();

                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                    throw new ApiException(e);
                }

                if (resultFromWebHook == null) {
                    resultFromWebHook = new ApiResult();
                    resultFromWebHook.code = Constants.POLLING_TIMEOUT_ERROR;
                    resultFromWebHook.message = String.format("polling result of api[%s] timeout after %s ms", action.getClass().getSimpleName(), timeout);
                }

                waittingApis.remove(jobUuid);

                return resultFromWebHook;
            }
        }

        private ApiResult webHookResult() {
            if (completion == null) {
                return syncWebHookResult();
            } else {
                return null;
            }
        }

        private HttpUrl.Builder fillApiRequestBuilderHead() {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme(config.scheme)
                    .host(config.hostname)
                    .port(config.port);

            if (config.contextPath != null) {
                urlBuilder.addPathSegments(config.contextPath);
            }

            urlBuilder.addPathSegments(info.path);

            urlBuilder.addPathSegment("v1");

            return urlBuilder;
        }

        private String[] s(String... strs) {
            return strs;
        }

        private Map<String, String[]> getCommonParamMap() {
            Map<String, String[]> vars = new HashMap<>();
            if (action.Action != null && !action.Action.isEmpty()) {
                vars.put(Constants.ACTION, s(action.Action));
            } else {
                vars.put(Constants.ACTION, s(action.getActionName("Action")));
            }
            vars.put(Constants.SECRET_ID, s(config.SecretId));
            vars.put(Constants.TIMESTAMP, s(String.format("%s", System.currentTimeMillis())));
            vars.put(Constants.NONCE, s(String.format("%s", System.currentTimeMillis() % 88888)));
            vars.put(Constants.SIGNATURE_METHOD, s(config.SignatureMethod));

            return vars;
        }


        private String getSignatureString(Map<String, String[]> vars) {

            List<String> params = vars.entrySet().stream()
                    .map(arg -> arg.getKey() + "=" + join(Arrays.asList(arg.getValue()), ""))
                    .collect(Collectors.toList());
            StringBuilder requestString = new StringBuilder(info.httpMethod);
            if (config.port == 80) {
                requestString.append(String.format("%s://%s/%s/%s", config.scheme, config.hostname, info.path, "v1"));
            } else {
                requestString.append(String.format("%s://%s:%s/%s/%s", config.scheme, config.hostname, config.port, info.path, "v1"));
            }


            requestString.append("?").append(join(params, "&"));

            String hmac = HMAC.encryptHMACString(requestString.toString(), config.SecretKey, vars.getOrDefault(Constants.SIGNATURE_METHOD, s("HmacMD5"))[0]);

            return HMAC.encryptBase64(hmac);
        }

        private void fillQueryApiRequestBuilder(Request.Builder reqBuilder) {
            QueryAction qaction = (QueryAction) action;

            Map<String, String[]> vars = new TreeMap<>(Comparator.comparing(String::toLowerCase));
            vars.putAll(getCommonParamMap());

            if (qaction.uuid != null) {
                vars.put("uuid", s(String.format("%s", qaction.uuid)));
            }
            if (!qaction.conditions.isEmpty()) {
                String[] q = qaction.conditions.toArray(new String[qaction.conditions.size()]);
                Arrays.sort(q);
                vars.put("q", q);
            }
            if (qaction.limit != null) {
                vars.put("limit", s(String.format("%s", qaction.limit)));
            }
            if (qaction.start != null) {
                vars.put("start", s(String.format("%s", qaction.start)));
            }
            if (qaction.count != null) {
                vars.put("count", s(String.format("%s", qaction.count)));
            }
            if (qaction.groupBy != null) {
                vars.put("groupBy", s(qaction.groupBy));
            }
            if (qaction.replyWithCount != null) {
                vars.put("replyWithCount", s(String.format("%s", qaction.replyWithCount)));
            }
            if (qaction.sortBy != null) {
                if (qaction.sortDirection == null) {
                    vars.put("sort", s(String.format("%s", qaction.sortBy)));
                } else {
                    String d = "asc".equals(qaction.sortDirection) ? "+" : "-";
                    vars.put("sort", s(String.format("%s%s", d, qaction.replyWithCount)));
                }
            }
            if (qaction.fields != null && !qaction.fields.isEmpty()) {
                vars.put("fields", s(join(qaction.fields, ",")));
            }

            vars.put(Constants.SIGNATURE, s(getSignatureString(vars)));

            HttpUrl.Builder urlBuilder = fillApiRequestBuilderHead();

            for (Map.Entry<String, String[]> entry : vars.entrySet()) {
                for (String v : entry.getValue()) {
                    urlBuilder.addQueryParameter(entry.getKey(), v);
                }
            }
            reqBuilder.url(urlBuilder.build()).get();
        }

        private void fillNonQueryApiRequestBuilder(Request.Builder reqBuilder) {

            Map<String, String[]> vars = new TreeMap<>(Comparator.comparing(String::toLowerCase));
            vars.putAll(getCommonParamMap());
            HttpUrl.Builder builder = fillApiRequestBuilderHead();
            for (String k : action.getAllParameterNames()) {

                if (Constants.SIGNATURE.equalsIgnoreCase(k) || vars.containsKey(k)) {
                    continue;
                }
                Object v = action.getParameterValue(k);
                if (v != null) {
                    if (v instanceof Collection) {
                        String[] ks = ((Collection<String>) v).toArray(new String[((Collection) v).size()]);
                        Arrays.sort(ks);
                        vars.put(k, ks);
                    } else if (v instanceof Map) {
                        for (Object o : ((Map) v).entrySet()) {
                            Map.Entry pe = (Map.Entry) o;
                            if (!(pe.getKey() instanceof String)) {
                                throw new ApiException(String.format("%s only supports map parameter whose keys and values are both string. %s.%s.%s is not key string",
                                        info.httpMethod, action.getClass(), k, pe.getKey()));
                            }

                            if (pe.getValue() instanceof Collection) {
                                for (Object i : (Collection) pe.getValue()) {
                                    vars.put(String.format("%s.%s", k, pe.getKey()), s(i.toString()));
                                }
                            } else {
                                vars.put(String.format("%s.%s", k, pe.getKey()), s(pe.getValue().toString()));
                            }
                        }
                    } else {

                        vars.put(k, s(v.toString()));
                    }
                }

            }

            vars.put(Constants.SIGNATURE, s(getSignatureString(vars)));

            for (Map.Entry<String, String[]> entry : vars.entrySet()) {
                for (String v : entry.getValue()) {
                    builder.addQueryParameter(entry.getKey(), v);
                }
            }

            switch (info.httpMethod) {
                case "GET":
                    reqBuilder.url(builder.build()).get();
                    break;
                default:
                    throw new RuntimeException("should not be here");
            }
        }

        private ApiResult pollResult(Response response) throws IOException {
            if (!info.needPoll) {
                throw new ApiException(String.format("[Internal Error] the api[%s] is not an async API but" +
                        " the server returns 201 status code", action.getClass().getSimpleName()));
            }

            Map body = gson.fromJson(response.body().string(), LinkedHashMap.class);
            String result = (String) body.get(Constants.RESULT);
            if (result == null) {
                throw new ApiException(String.format("Internal Error] the api[%s] is an async API but the server" +
                        " doesn't return the polling location url", action.getClass().getSimpleName()));
            }

            if (completion == null) {
                // sync polling
                return syncPollResult(result);
            } else {
                // async polling
                asyncPollResult(result);
                return null;
            }
        }

        private void asyncPollResult(final String url) {
            final long current = System.currentTimeMillis();
            final Long timeout = this.getTimeout();
            final long expiredTime = current + timeout;
            final Long i = this.getInterval();

            final Object sessionId = action.getParameterValue(Constants.SESSION_ID);
            final Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                long count = current;
                long interval = i;

                private void done(ApiResult res) {
                    completion.complete(res);
                    timer.cancel();
                }

                @Override
                public void run() {
                    Request req = new Request.Builder()
                            .url(url)
                            .addHeader(Constants.HEADER_AUTHORIZATION, String.format("%s %s", Constants.OAUTH, sessionId))
                            .addHeader(Constants.HEADER_JSON_SCHEMA, Boolean.TRUE.toString())
                            .get()
                            .build();

                    try {
                        try (Response response = http.newCall(req).execute()) {
                            if (response.code() != 200 && response.code() != 503 && response.code() != 202) {
                                done(httpError(response.code(), response.body().string()));
                                return;
                            }

                            // 200 means the task has been completed successfully,
                            // or a 505 indicates a failure,
                            // otherwise a 202 returned means it is still
                            // in processing
                            if (response.code() == 200 || response.code() == 503) {
                                done(writeApiResult(response));
                                return;
                            }

                            count += interval;
                            if (count >= expiredTime) {
                                ApiResult res = new ApiResult();
                                /*res.error = errorCode(
                                        Constants.POLLING_TIMEOUT_ERROR,
                                        "timeout of polling async API result",
                                        String.format("polling result of api[%s] timeout after %s ms", action.getClass().getSimpleName(), timeout)
                                );*/
                                res.code = Constants.POLLING_TIMEOUT_ERROR;
                                res.message = String.format("polling result of api[%s] timeout after %s ms", action.getClass().getSimpleName(), timeout);

                                done(res);
                            }
                        }
                    } catch (Throwable e) {
                        //TODO: logging

                        ApiResult res = new ApiResult();
                        /*res.error = errorCode(
                                Constants.INTERNAL_ERROR,
                                "an internal error happened",
                                e.getMessage()
                        );*/
                        res.code = Constants.INTERNAL_ERROR;
                        res.message = e.getMessage();

                        done(res);
                    }
                }
            }, 0, i);
        }

        private ErrorCode errorCode(String id, String s, String d) {
            ErrorCode err = new ErrorCode();
            err.code = id;
            err.description = s;
            err.details = d;
            return err;
        }

        private void fillApiResultBuilder(Request.Builder reqBuilder, String resultUuid) {
            Map<String, String[]> vars = new TreeMap<>(Comparator.comparing(String::toLowerCase));
            vars.put(Constants.ACTION, s(Constants.ASYNC_JOB_ACTION));
            vars.put(Constants.TIMESTAMP, s(String.format("%s", System.currentTimeMillis())));
            vars.put(Constants.NONCE, s(String.format("%s", System.currentTimeMillis() % 88888)));
            vars.put("uuid", s(resultUuid));

            HttpUrl.Builder urlBuilder = fillApiRequestBuilderHead();
            for (Map.Entry<String, String[]> entry : vars.entrySet()) {
                for (String v : entry.getValue()) {
                    urlBuilder.addQueryParameter(entry.getKey(), v);
                }
            }
            reqBuilder.url(urlBuilder.build()).get();
        }

        private ApiResult syncPollResult(String resultUuid) {
            long current = System.currentTimeMillis();
            Long timeout = this.getTimeout();
            long expiredTime = current + timeout;
            Long interval = this.getInterval();

            while (current < expiredTime) {
                Request.Builder builder = new Request.Builder()
                        .addHeader(Constants.HEADER_JSON_SCHEMA, Boolean.TRUE.toString());

                fillApiResultBuilder(builder, resultUuid);

                Request req = builder.build();

                try {
                    try (Response response = http.newCall(req).execute()) {
                        if (response.code() != 200 && response.code() != 503 && response.code() != 202) {
                            return httpError(response.code(), response.body().string());
                        }

                        // 200 means the task has been completed
                        // otherwise a 202 returned means it is still
                        // in processing
                        if (response.code() == 200 || response.code() == 503) {
                            return writeApiResult(response);
                        }

                        TimeUnit.MILLISECONDS.sleep(interval);
                        current += interval;
                    }
                } catch (InterruptedException e) {
                    //ignore
                } catch (IOException e) {
                    throw new ApiException(e);
                }
            }

            ApiResult res = new ApiResult();
            res.code = Constants.POLLING_TIMEOUT_ERROR;
            res.message = String.format("polling result of api[%s] timeout after %s ms", action.getClass().getSimpleName(), timeout);

            return res;
        }

        private ApiResult writeApiResult(Response response) throws IOException {
            ApiResult res = new ApiResult();

            if (response.code() == 200) {
                res.setResultString(response.body().string());
            } else if (response.code() == 503) {
                res = gson.fromJson(response.body().string(), ApiResult.class);
            } else {
                throw new ApiException(String.format("unknown status code: %s", response.code()));
            }
            return res;
        }

        private ApiResult httpError(int code, String details) {
            ApiResult res = new ApiResult();
            /*res.error = errorCode(
                    Constants.HTTP_ERROR,
                    String.format("the http status code[%s] indicates a failure happened", code),
                    details
            );*/
            res.code = Constants.HTTP_ERROR;
            res.message = details;

            return res;
        }

        ApiResult call() {
            return doCall();
        }

        private long getTimeout() {
            Long timeout = (Long) action.getNonAPIParameterValue("timeout", false);
            return timeout == ACTION_DEFAULT_TIMEOUT ? config.defaultPollingTimeout : timeout;
        }

        private long getInterval() {
            Long interval = (Long) action.getNonAPIParameterValue("pollingInterval", false);
            return interval == ACTION_DEFAULT_POLLINGINTERVAL ? config.defaultPollingInterval : interval;
        }
    }

    private static void errorIfNotConfigured() {
        if (config == null) {
            throw new RuntimeException("setConfig() must be called before any methods");
        }
    }

    static void call(AbstractAction action, InternalCompletion completion) {
        errorIfNotConfigured();
        new Api(action).call(completion);
    }

    static ApiResult call(AbstractAction action) {
        errorIfNotConfigured();
        return new Api(action).call();
    }


}
