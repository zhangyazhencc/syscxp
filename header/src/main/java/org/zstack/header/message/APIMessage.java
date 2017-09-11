package org.zstack.header.message;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.rest.APINoSee;
import org.zstack.utils.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class APIMessage extends NeedReplyMessage {
    /**
     * @ignore
     */
    @NoJsonSchema
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


    public Map<String, Object> getDeclaredFieldAndValues() {
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Object> msgFields = new HashMap<>();

        for (Field field: fields) {
            Object value = FieldUtils.getFieldValue(field.getName(), this);
            msgFields.put(field.getName(), value);
        }
        return msgFields;
    }

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
