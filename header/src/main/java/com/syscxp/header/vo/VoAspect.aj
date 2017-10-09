package com.syscxp.header.vo;

import com.syscxp.header.core.keyvalue.KeyValueEntity;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.BeanUtils;
import com.syscxp.utils.FieldUtils;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public aspect VoAspect {
    pointcut completeVO(Object entity) : target(entity) && execution((@javax.persistence.Entity Object)+.new(..));
    pointcut completeKeyValueEntity(Object entity) : target(entity) && execution(com.syscxp.header.core.keyvalue.KeyValueEntity+.new(..));

    private static Map<Class, Field> objectUuidMap =  new ConcurrentHashMap<Class, Field>();

    static {
        List<Class> entities = BeanUtils.scanClass("com.syscxp", Entity.class);
        entities.addAll(BeanUtils.scanClassByType("com.syscxp", KeyValueEntity.class));
        for (Class entity : entities) {
            Field uuidField = FieldUtils.getAnnotatedField(Uuid.class, entity);
            if (uuidField == null) {
                continue;
            }

            if (uuidField.getType() != String.class) {
                throw new CloudRuntimeException(String.format("field annotated by @Uuid must be type of String, but %s.%s is %s", entity.getName(), uuidField.getName(), uuidField.getType().getName()));
            }

            uuidField.setAccessible(true);
            objectUuidMap.put(entity, uuidField);
        }
    }

    private void completeField(Object entity) {
        Class<?> currClass = entity.getClass();
        Field uuidField = objectUuidMap.get(currClass);
        if (uuidField == null) {
            return;
        }

        try {
            if (uuidField.get(entity) == null) {
                uuidField.set(entity, UUID.randomUUID().toString().replace("-", ""));
            }
        } catch (Exception e) {
            throw new CloudRuntimeException(String.format("unable to evaluate uuid field %s.%s, because %s", currClass.getName(), uuidField.getName(), e.getMessage()), e);
        }
    }

    after(Object entity) returning : completeVO(entity) || completeKeyValueEntity(entity) {
        completeField(entity);
    }
}
