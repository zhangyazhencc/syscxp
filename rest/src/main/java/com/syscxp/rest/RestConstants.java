package com.syscxp.rest;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 14:21
 * Author: wj
 */
public interface RestConstants {
    String API_VERSION = "/v1";
    String ALL_PATH = "/v1/**";
    String ASYNC_JOB_ACTION = "ApiResult";

    String HEADER_JSON_SCHEMA = "X-JSON-Schema";
    String HEADER_WEBHOOK = "X-Web-Hook";
    String HEADER_JOB_UUID = "X-Job-UUID";
    String HEADER_JOB_SUCCESS = "X-Job-Success";
    String HEADER_OAUTH = "OAuth";

}
