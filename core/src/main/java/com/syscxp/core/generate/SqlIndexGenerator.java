package com.syscxp.core.generate;

import org.apache.commons.io.FileUtils;
import com.syscxp.header.configuration.APIGenerateSqlIndexMsg;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.vo.EO;
import com.syscxp.header.vo.Index;
import com.syscxp.utils.BeanUtils;
import com.syscxp.utils.FieldUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 */
public class SqlIndexGenerator {
    private static CLogger logger = Utils.getLogger(SqlIndexGenerator.class);

    private class IndexInfo {
        Class entity;
        Field indexField;

        IndexInfo(Class entity, Field f) {
            this.entity = entity;
            indexField = f;
        }

        String toIndexSql() {
            Index idx = indexField.getAnnotation(Index.class);
            if (String.class.isAssignableFrom(indexField.getType()) && idx.length() != -1) {
                return String.format("CREATE INDEX %s ON %s (%s(%s));",
                        String.format("idx%s%s", entity.getSimpleName(), indexField.getName()),
                        entity.getSimpleName(),
                        indexField.getName(),
                        idx.length()
                );
            } else {
                return String.format("CREATE INDEX %s ON %s (%s);",
                        String.format("idx%s%s", entity.getSimpleName(), indexField.getName()),
                        entity.getSimpleName(),
                        indexField.getName()
                );
            }
        }
    }

    private String outputPath;
    private List<String> basePkgs;

    private List<Class> entityClass = new ArrayList<>();
    private Map<Class, List<IndexInfo>> indexMap = new HashMap<>();
    private StringBuilder writer = new StringBuilder();

    public SqlIndexGenerator(APIGenerateSqlIndexMsg msg) {
        outputPath = msg.getOutputPath();
        if (outputPath == null) {
            outputPath = PathUtil.join(System.getProperty("user.home"), "syscxp-sql", "indexes.sql");
        }
        basePkgs = msg.getBasePackageNames();
        if (basePkgs == null) {
            basePkgs = Collections.singletonList("com.syscxp");
        }
    }

    public void generate() {
        for (String pkgName: basePkgs) {
            entityClass.addAll(BeanUtils.scanClass(pkgName, Entity.class));
        }

        for (Class entity : entityClass) {
            collectIndex(entity);
        }


        generateIndex();
    }

    private void generateIndex() {
        List<Class> classes = new ArrayList<>(indexMap.keySet());
        classes.sort(Comparator.comparing(Class::getSimpleName));
        
        for (Class clz : classes) {
            generateIndexForEntity(indexMap.get(clz));
        }

        try {
            FileUtils.writeStringToFile(new File(outputPath), writer.toString());
        } catch (IOException e) {
            throw new CloudRuntimeException(e);
        }
    }

    private void generateIndexForEntity(List<IndexInfo> keys) {
        if (keys.isEmpty()) {
            return;
        }

        writer.append(String.format("\n# Index for table %s\n", keys.get(0).entity.getSimpleName()));
        for (IndexInfo key : keys) {
            writer.append(String.format("\n%s", key.toIndexSql()));
        }
        writer.append("\n");
    }


    private void collectIndex(Class entity) {
        List<Field> fs;
        Class superClass = entity.getSuperclass();
        if (superClass.isAnnotationPresent(Entity.class) || entity.isAnnotationPresent(EO.class)) {
            // parent class or EO class is also an entity, it will take care of its foreign key,
            // so we only do our own foreign keys;
            fs = FieldUtils.getAnnotatedFieldsOnThisClass(Index.class, entity);
        } else {
            fs = FieldUtils.getAnnotatedFields(Index.class, entity);
        }
        List<IndexInfo> keyInfos = indexMap.computeIfAbsent(entity, k -> new ArrayList<>());
        /*List<IndexInfo> keyInfos = indexMap.get(entity);
        if (keyInfos == null) {
            keyInfos = new ArrayList<>();
            indexMap.put(entity, keyInfos);
        }*/

        for (Field f : fs) {
            keyInfos.add(new IndexInfo(entity, f));
        }
    }
}
