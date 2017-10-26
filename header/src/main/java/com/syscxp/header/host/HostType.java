package com.syscxp.header.host;

import java.util.*;

public class HostType {
    private static Map<String, HostType> types = Collections.synchronizedMap(new HashMap<String, HostType>());
    private final String typeName;
    private boolean exposed = true;

    public HostType(String typeName) {
        this.typeName = typeName;
        types.put(typeName, this);
    }

    public HostType(String typeName, boolean exposed) {
        this(typeName);
        this.exposed = exposed;
    }

    public static boolean hasType(String type) {
        return types.keySet().contains(type);
    }

    public static HostType valueOf(String typeName) {
        HostType type = types.get(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Host type: " + typeName + " was not registered by any HostFactory");
        }
        return type;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    @Override
    public String toString() {
        return typeName;
    }

    @Override
    public boolean equals(Object t) {
        if (t == null || !(t instanceof HostType)) {
            return false;
        }

        HostType type = (HostType) t;
        return type.toString().equals(typeName);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    public static Set<String> getAllTypeNames() {
        HashSet<String> exposedTypes = new HashSet<String>();
        for (HostType type : types.values()) {
            if (type.exposed) {
                exposedTypes.add(type.toString());
            }
        }
        return exposedTypes;
    }
}
