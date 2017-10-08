package com.syscxp.core.config;

import com.syscxp.header.message.NeedJsonSchema;

/**
 */
public class GlobalConfigCanonicalEvents {
    public static final String UPDATE_EVENT_PATH = "/globalConfig/update/{category}/{name}/{nodeUuid}";

    @NeedJsonSchema
    public static class UpdateEvent {
        private String oldValue;
        private String newValue;

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
    }
}
