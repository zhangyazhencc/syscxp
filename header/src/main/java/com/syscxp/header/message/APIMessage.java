package com.syscxp.header.message;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.header.rest.APIWithSession;
import com.syscxp.utils.FieldUtils;
import com.syscxp.utils.gson.JSONObjectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class APIMessage extends NeedReplyMessage {
    /**
     * @ignore
     */
    @NoJsonSchema
    @APIWithSession
    @APINoSee
    private SessionInventory session;

    private String ip;

    public SessionInventory getSession() {
        return session;
    }

    public void setSession(SessionInventory session) {
        this.session = session;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getDeclaredFieldAndValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Object> msgFields = new HashMap<>();

        for (Field field: fields) {
            Object value = FieldUtils.getFieldValue(field.getName(), this);
            msgFields.put(field.getName(), value);
        }
        return JSONObjectUtil.toJsonString(msgFields);
    }

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
