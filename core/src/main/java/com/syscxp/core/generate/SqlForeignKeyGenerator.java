package com.syscxp.core.generate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import com.syscxp.header.configuration.APIGenerateSqlForeignKeyMsg;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.vo.EO;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;
import com.syscxp.utils.BeanUtils;
import com.syscxp.utils.FieldUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.Pair;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 */
public class SqlForeignKeyGenerator {
    private static CLogger logger = Utils.getLogger(SqlForeignKeyGenerator.class);
    private Map<Class, List<String>> entityForeignKeyIndexMap = new HashMap<>();

    private String outputPath;
    private List<String> basePkgs;
    private List<Class> entityClass = new ArrayList<>();
    private Map<Class, List<ForeignKeyInfo>> keyMap = new HashMap<>();
    private Map<String, ForeignKeyInfo> allKeys = new HashMap<>();
    private StringBuilder writer = new StringBuilder();

    public SqlForeignKeyGenerator() {
        outputPath = PathUtil.join(System.getProperty("user.home"), "syscxp-sql", "foreignKeys.sql");
        basePkgs = Collections.singletonList("com.syscxp");
    }

    SqlForeignKeyGenerator(APIGenerateSqlForeignKeyMsg msg) {
        outputPath = msg.getOutputPath();
        if (outputPath == null) {
            outputPath = PathUtil.join(System.getProperty("user.home"), "syscxp-sql", "foreignKeys.sql");
        }
        basePkgs = msg.getBasePackageNames();
        if (basePkgs == null) {
            basePkgs = Collections.singletonList("com.syscxp");
        }
    }

    public List<Pair<String, String>> generateEORelations() {

        for (String pkgName : basePkgs) {
            entityClass.addAll(BeanUtils.scanClass(pkgName, Entity.class));
        }

        for (Class entity : entityClass) {
            collectForeignKeys(entity);
        }

        List<Class> classes = new ArrayList<>(keyMap.keySet());
        classes.sort(Comparator.comparing(Class::getSimpleName));

        Map<String, Pair<String, String>> m = new HashMap<>();
        for (Class clz : classes) {
            for (ForeignKeyInfo f : keyMap.get(clz)) {
                Pair<String, String> p = f.makeEOForeignKeyRelations();
                if (p != null) {
                    m.put(p.toString(), p);
                }
            }
        }

        return new ArrayList<>(m.values());
    }

    private class ForeignKeyInfo {
        String fullName;
        int order;
        Class entity;
        String parentKey;
        Class parentClass;
        ReferenceOption onDeleteAction;
        ReferenceOption onUpdateAction;
        String childKey;

        ForeignKeyInfo(Class entity, Field f) {
            ForeignKey annotation = f.getAnnotation(ForeignKey.class);
            if ("".equals(annotation.parentKey())) {
                Field parentKeyField = FieldUtils.getAnnotatedField(Id.class, annotation.parentEntityClass());
                parentKey = parentKeyField.getName();
            } else {
                parentKey = annotation.parentKey();
            }
            parentClass = annotation.parentEntityClass();
            onDeleteAction = annotation.onDeleteAction();
            onUpdateAction = annotation.onUpdateAction();
            fullName = String.format("%s.%s", annotation.parentEntityClass().getSimpleName(), parentKey);
            childKey = f.getName();
            this.entity = entity;
        }

        ForeignKeyInfo(Class childEntity) {
            Class superClass = childEntity.getSuperclass();
            EO eo = (EO) superClass.getAnnotation(EO.class);
            if (eo != null) {
                superClass = eo.EOClazz();
            }

            Field priKeyField = FieldUtils.getAnnotatedField(Id.class, superClass);
            parentClass = superClass;
            entity = childEntity;
            parentKey = priKeyField.getName();
            childKey = priKeyField.getName();
            onUpdateAction = ReferenceOption.RESTRICT;
            onDeleteAction = ReferenceOption.CASCADE;
            fullName = String.format("%s.%s", superClass.getSimpleName(), parentKey);
        }

        @Override
        public boolean equals(Object t) {
            return t instanceof ForeignKeyInfo && fullName.equals(((ForeignKeyInfo) t).fullName);

        }

        @Override
        public int hashCode() {
            return fullName.hashCode();
        }

        private String makeReferenceAction() {
            List<String> strs = new ArrayList<String>();
            if (ReferenceOption.NO_ACTION != onUpdateAction) {
                strs.add(onUpdateAction.toOnUpdateSql());
            }
            if (ReferenceOption.NO_ACTION != onDeleteAction) {
                strs.add(onDeleteAction.toOnDeleteSql());
            }
            return StringUtils.join(strs, " ");
        }

        private Pair<String, String> makeForeignKeyRelations() {
            return new Pair<>(parentClass.getSimpleName(), entity.getSimpleName());
        }

        private Pair<String, String> makeEOForeignKeyRelations() {
            if (onDeleteAction != ReferenceOption.RESTRICT) {
                return null;
            }
            if (entity.getSimpleName().endsWith("EO") && parentClass.getSimpleName().endsWith("EO")) {
                return new Pair<>(parentClass.getSimpleName(), entity.getSimpleName());
            }
            return null;
        }

        private String makeForeignKeyName() {
            String noIndexKeyName = String.format("fk%s%s", entity.getSimpleName(), parentClass.getSimpleName());
            /*List<String> keys = entityForeignKeyIndexMap.get(entity);
            if (keys == null) {
                keys = new ArrayList<>();
                entityForeignKeyIndexMap.put(entity, keys);
            }*/

            List<String> keys = entityForeignKeyIndexMap.computeIfAbsent(entity, k -> new ArrayList<>());
            int count = 0;
            for (String key : keys) {
                if (noIndexKeyName.equals(key)) {
                    count ++;
                }
            }

            keys.add(noIndexKeyName);

            return count == 0 ? noIndexKeyName : String.format("%s%s", noIndexKeyName, count);
        }

        String toForeignKeySql() {
            return String.format("ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s) %s;",
                    entity.getSimpleName(),
                    makeForeignKeyName(),
                    childKey,
                    parentClass.getSimpleName(),
                    parentKey,
                    makeReferenceAction()
            );
        }
    }

    public void generate() {
        for (String pkgName: basePkgs) {
            entityClass.addAll(BeanUtils.scanClass(pkgName, Entity.class));
        }

        for (Class entity : entityClass) {
            collectForeignKeys(entity);
        }

        orderAllKeys();

        generateForeignKeys();
    }

    private void generateForeignKeys() {
        List<Class> classes = new ArrayList<>(keyMap.keySet());
        classes.sort(Comparator.comparing(Class::getSimpleName));

        for (Class clz : classes) {
            generateForeignKeyForEntity(clz, keyMap.get(clz));
        }

        try {
            FileUtils.writeStringToFile(new File(outputPath), writer.toString());
        } catch (IOException e) {
            throw new CloudRuntimeException(e);
        }
    }

    private void evaluateOrder(ForeignKeyInfo key) {
        ForeignKeyInfo ordered = allKeys.get(key.fullName);
        key.order = ordered.order;
    }

    private void generateForeignKeyForEntity(Class entity, List<ForeignKeyInfo> keys) {
        if (keys.isEmpty()) {
            return;
        }

        for (ForeignKeyInfo key : keys) {
            evaluateOrder(key);
        }

        keys.sort(Comparator.comparingInt(o -> o.order));

        writer.append(String.format("\n# Foreign keys for table %s\n", entity.getSimpleName()));
        for (ForeignKeyInfo key : keys) {
            writer.append(String.format("\n%s", key.toForeignKeySql()));
        }
        writer.append("\n");
    }

    private void orderAllKeys() {
        List<ForeignKeyInfo> orderKeys = new ArrayList<>(allKeys.values());
        orderKeys.sort(Comparator.comparing(o -> o.fullName));

        for (ForeignKeyInfo keyInfo : orderKeys) {
            keyInfo.order = orderKeys.indexOf(keyInfo);
            logger.debug(String.format("foreign key: %s, order: %s", keyInfo.fullName, keyInfo.order));
        }
    }

    private void collectForeignKeys(Class entity) {
        List<Field> fs;
        Class superClass = entity.getSuperclass();
        if (superClass.isAnnotationPresent(Entity.class) || entity.isAnnotationPresent(EO.class)) {
            // parent class or EO class is also an entity, it will take care of its foreign key,
            // so we only do our own foreign keys;
            fs = FieldUtils.getAnnotatedFieldsOnThisClass(ForeignKey.class, entity);
        } else {
            fs = FieldUtils.getAnnotatedFields(ForeignKey.class, entity);
        }

        /*List<ForeignKeyInfo> keyInfos = keyMap.get(entity);
        if (keyInfos == null) {
            keyInfos = new ArrayList<>();
            keyMap.put(entity, keyInfos);
        }*/
        List<ForeignKeyInfo> keyInfos = keyMap.computeIfAbsent(entity, k -> new ArrayList<>());

        for (Field f : fs) {
            ForeignKeyInfo keyInfo = new ForeignKeyInfo(entity, f);
            if (!allKeys.containsKey(keyInfo.fullName)) {
                allKeys.put(keyInfo.fullName, keyInfo);
            }
            keyInfos.add(new ForeignKeyInfo(entity, f));
        }

        if (superClass.isAnnotationPresent(Entity.class)) {
            ForeignKeyInfo priInfo = new ForeignKeyInfo(entity);
            if (!allKeys.containsKey(priInfo.fullName)) {
                allKeys.put(priInfo.fullName, priInfo);
            }
            keyInfos.add(priInfo);
        }
    }
}
