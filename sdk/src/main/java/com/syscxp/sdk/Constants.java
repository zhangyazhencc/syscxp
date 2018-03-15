package com.syscxp.sdk;

import okhttp3.MediaType;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:03
 * Author: wj
 */
interface Constants {
    String SESSION_ID = "sessionId";
    String HEADER_AUTHORIZATION = "Authorization";
    String OAUTH = "OAuth";
    String LOCATION = "location";

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    String HTTP_ERROR = "sdk.1000";
    String POLLING_TIMEOUT_ERROR = "sdk.1001";
    String INTERNAL_ERROR = "sdk.1002";

    String HEADER_JSON_SCHEMA = "X-JSON-Schema";
    String HEADER_JOB_UUID = "X-Job-UUID";
    String HEADER_WEBHOOK = "X-Web-Hook";
    String HEADER_JOB_SUCCESS = "X-Job-Success";

    String SIGNATURE = "Signature";
    String SECRET_ID = "SecretId";
    String TIMESTAMP = "Timestamp";
    String ACTION = "Action";
    String NONCE = "Nonce";
    String SIGNATURE_METHOD = "SignatureMethod";

    String SESSION_UUID = "sessionUuid";
}
