package com.syscxp.header.message;

import com.syscxp.header.rest.APINoSee;

public abstract class Event extends Message {
    /**
     * @ignore
     */
    @APINoSee
    private String avoidKey;

    public String getAvoidKey() {
        return avoidKey;
    }

    public void setAvoidKey(String avoidKey) {
        this.avoidKey = avoidKey;
    }

    public abstract Type getType(String busProjectId);

    public abstract String getSubCategory();

    public static final String BINDING_KEY_PERFIX = "key.event.";

    public static enum Category {
        LOCAL,
        API,
    }

    public static class Type {
        private final String _name;

        public Type(String busProjectId, Category ctg, String subCtg) {
            _name = BINDING_KEY_PERFIX + busProjectId + "." + ctg.toString() + "." + subCtg;
        }

        @Override
        public String toString() {
            return _name;
        }

        @Override
        public int hashCode() {
            return _name.hashCode();
        }

        @Override
        public boolean equals(Object t) {
            if (!(t instanceof Type)) {
                return false;
            }

            Type type = (Type) t;
            return _name.equals(type.toString());
        }
    }
}
