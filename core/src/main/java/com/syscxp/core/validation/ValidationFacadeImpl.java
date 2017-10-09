package com.syscxp.core.validation;

import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.Component;
import com.syscxp.header.core.validation.Validator;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.utils.TypeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ValidationFacadeImpl implements ValidationFacade, Component {
    private Map<Class, List<Validator>> validators = new HashMap<Class, List<Validator>>();

    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private ErrorFacade errf;

    @Override
    public void validateErrorByException(Object obj) {
        ErrorCode err = validateErrorByErrorCode(obj);
        if (err != null) {
            throw new OperationFailureException(err);
        }
    }

    @Override
    public ErrorCode validateErrorByErrorCode(Object obj) {
        if (obj instanceof ConditionalValidation) {
            ConditionalValidation cond = (ConditionalValidation) obj;
            if (!cond.needValidation()) {
                return null;
            }
        }

        List<Class> classes = TypeUtils.getAllClassOfObject(obj);
        for (Class clz : classes) {
            List<Validator> vs = validators.get(clz);
            if (vs != null) {
                for (Validator v : vs) {
                    String err = v.validate(obj);
                    if (err != null) {
                        return errf.stringToInternalError(err);
                    }
                }
            }
        }

        return null;
    }

    private void populateExtensions() {
        for (Validator ext : pluginRgty.getExtensionList(Validator.class)) {
            for (Class clazz : ext.supportedClasses()) {
                List<Validator> vs = validators.get(clazz);
                if (vs == null) {
                    vs = new ArrayList<Validator>();
                    validators.put(clazz, vs);
                }
                vs.add(ext);
            }
        }
    }

    @Override
    public boolean start() {
        populateExtensions();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
