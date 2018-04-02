package com.syscxp.rest;

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
    private String code = "OK";
    private String message = "success";
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        put("code", code);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        put("message", message);
    }
}
