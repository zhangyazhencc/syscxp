package com.syscxp.header.message;

abstract public class LocalEvent extends Event {
    @NoJsonSchema
    private Type type = null;

    @Override
    public final Type getType(String busProjectId) {
        if (type == null) {
            type = new Type(busProjectId, Event.Category.LOCAL, getSubCategory());
        }
        return type;
    }
}
