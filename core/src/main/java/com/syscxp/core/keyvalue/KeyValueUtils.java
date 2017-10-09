package com.syscxp.core.keyvalue;

import com.syscxp.utils.TypeUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 */
public class KeyValueUtils {
    public static boolean isPrimitiveTypeForKeyValue(Class type) {
        if (TypeUtils.isPrimitiveOrWrapper(type)) {
            return true;
        }

        if (TypeUtils.isTypeOf(type, Date.class, Timestamp.class)) {
            return true;
        }

        return false;
    }
}
