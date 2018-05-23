package com.syscxp.header.message;

import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.utils.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class NeedReplyMessage extends Message {
    /**
     * @desc in millisecond. Any reply/event received after timeout will be dropped
     * @optional
     */
    @APINoSee
    protected long timeout = -1;

    @APINoSee
    private String ip = "127.0.0.1";

    public NeedReplyMessage() {
        super();
    }

    public NeedReplyMessage(long timeout) {
        super();
    }

    public String toErrorString() {
        return String.format("Message[name: %s, id: %s] timeout after %s seconds",
                this.getClass().getName(),
                this.getId(),
                TimeUnit.MILLISECONDS.toSeconds(getTimeout()));
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
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
            if (field.isAnnotationPresent(PasswordNoSee.class))
                continue;
            Object value = FieldUtils.getFieldValue(field.getName(), this);
            msgFields.put(field.getName(), value);
        }
        return msgFields;
    }
}
