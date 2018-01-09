package com.syscxp.rest;

import com.syscxp.header.errorcode.ErrorCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:04
 * Author: wj
 */
public class ApiResponse extends HashMap {
    private String result;
    private ErrorCode error;
    private Map<String, String> schema;

    public String getResult() {
        return result;
    }

    public Map<String, String> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
        put("schema", schema);
    }

    public void setResult(String result) {
        this.result = result;
        put("result", result);
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
        put("error", error);
    }
}
