package com.syscxp.core.validation;

import com.syscxp.header.core.validation.Validation;
import com.syscxp.header.core.validation.Validator;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.FieldUtils;
import com.syscxp.utils.TypeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class DefaultValidator implements Validator {
    @Override
    public List<Class> supportedClasses() {
        List<Class> classes = new ArrayList<Class>();
        classes.add(Object.class);
        return classes;
    }

    private String error(Object obj, Field f, String msg) {
        return String.format("[validation error on class[%s], field[%s]]: %s", obj.getClass(), f.getName(), msg);
    }

    private String validate(Object obj, Field f) {
        try {
            f.setAccessible(true);
            Object value = f.get(obj);
            Validation at = f.getAnnotation(Validation.class);

            if (at.notNull() && value == null) {
                return error(obj, f, "field can not be null");
            }
            if (at.notZero() && TypeUtils.isTypeOf(value, Integer.TYPE, Integer.class, Long.TYPE, Long.class)) {
                if (value != null) {
                    long intValue = Long.valueOf(value.toString());
                    if (intValue == 0) {
                        return error(obj, f, "field can not be zero");
                    }
                }
            }

            return null;
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    @Override
    public String validate(Object obj) {
        List<Field> fs = FieldUtils.getAnnotatedFields(Validation.class, obj.getClass());
        for (Field f : fs) {
            String err = validate(obj, f);
            if (err != null) {
                return err;
            }
        }

        return null;
    }
}
