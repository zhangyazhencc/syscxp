package com.syscxp.core.db;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class DbGlobalProperty {
    @GlobalProperty(name="entityPackages", defaultValue = "com.syscxp")
    public static String ENTITY_PACKAGES;
}
