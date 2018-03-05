package com.syscxp.core.keyvalue;

import com.syscxp.utils.TypeUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 */
public class KeyValueUtils {
    public static boolean isPrimitiveTypeForKeyValue(Class type) {
        return TypeUtils.isPrimitiveOrWrapper(type) || TypeUtils.isTypeOf(type, Date.class, Timestamp.class);
    }
}
