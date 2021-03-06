package com.syscxp.header.message;

import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.header.rest.APIWithSession;
import com.syscxp.utils.FieldUtils;

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

    public SessionInventory getSession() {
        return session;
    }

    public void setSession(SessionInventory session) {
        this.session = session;
    }

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public static class InvalidApiMessageException extends RuntimeException {
        private Object[] arguments = new Object[]{};

        public InvalidApiMessageException(Object[] arguments) {
            this.arguments = arguments;
        }

        public InvalidApiMessageException(String message, Object...arguments) {
            super(message);
            this.arguments = arguments;
        }

        public InvalidApiMessageException(String message, Throwable cause, Object[] arguments) {
            super(message, cause);
            this.arguments = arguments;
        }

        public InvalidApiMessageException(Throwable cause, Object[] arguments) {
            super(cause);
            this.arguments = arguments;
        }

        public InvalidApiMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] arguments) {
            super(message, cause, enableSuppression, writableStackTrace);
            this.arguments = arguments;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public void setArguments(Object[] arguments) {
            this.arguments = arguments;
        }
    }

}
