package com.syscxp.header.notification;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.utils.DebugUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xing5 on 2017/3/16.
 */
public abstract class ApiNotification {
    private List<Inner> inners = new ArrayList<>();

    public class Inner {
        String content;
        String accountUuid;
        Object[] arguments;
        String resourceUuid;
        String resourceType;
        APIMessage message;
        APIEvent event;
        Boolean success;
        Map<String, Object> context = new HashMap<>();

        public APIMessage getMessage() {
            return message;
        }

        public String getAccountUuid() {
            return accountUuid;
        }

        public APIEvent getEvent() {
            return event;
        }

        public String getContent() {
            return content;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public String getResourceUuid() {
            return resourceUuid;
        }

        public String getResourceType() {
            return resourceType;
        }

        public Boolean getSuccess() {
            return success;
        }

        public Map<String, Object> getContext() {
            return context;
        }


        public Inner(String content, Object[] arguments) {
            this.content = content;
            this.arguments = arguments;
        }

        public Inner context(String key, Object value) {
            context.put(key, value);
            return this;
        }

        public Inner owner(String key, Object value) {
            context.put(key, value);
            return this;
        }

        public Inner resource(String uuid, String type) {
            resourceUuid = uuid;
            resourceType = type;
            return this;
        }

        public Inner messageAndEvent(APIMessage msg, APIEvent evt) {
            message = msg;
            event = evt;
            success = evt.isSuccess();
            return this;
        }

        public void done() {
            DebugUtils.Assert(success != null, "you must call successOrNot() before done()");
            inners.add(this);
        }
    }

    protected Inner ntfy(String content, Object... args) {
        return new Inner(content, args);
    }

    public abstract void after(APIEvent evt);

    public void before() {
    }

    public List<Inner> getInners() {
        return inners;
    }

    public void setInners(List<Inner> inners) {
        this.inners = inners;
    }
}
