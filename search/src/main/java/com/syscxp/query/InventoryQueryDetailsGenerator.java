package com.syscxp.query;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.search.Inventory;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InventoryQueryDetailsGenerator {
    private static final CLogger logger = Utils.getLogger(InventoryQueryDetailsGenerator.class);

    private static void generateInventoryDetails(String folder, Class<?> inventoryClass) throws IOException {
        String filePath = PathUtil.join(folder, inventoryClass.getSimpleName());
        Class<?> currentClass = inventoryClass;
        StringBuilder sb = new StringBuilder();
        do {
            for (Field f : currentClass.getDeclaredFields()) {
                String info = String.format("%s : %s\n", f.getName(), f.getType().getName());
                sb.append(info);
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != Object.class);
        
        FileUtils.writeStringToFile(new File(filePath), sb.toString());
    }

    public static void generate(String exportPath, List<String> basePkgs) {
        try {
            if (exportPath == null) {
                exportPath = PathUtil.join(System.getProperty("user.home"), "syscxp-inventory-query-details");
            }

            if (basePkgs == null || basePkgs.isEmpty()) {
                basePkgs = new ArrayList<String>(1);
                basePkgs.add("com.syscxp");
            }

            FileUtils.deleteDirectory(new File(exportPath));
            File folder = new File(exportPath);
            folder.mkdirs();

            String folderName = folder.getAbsolutePath();
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Inventory.class));
            scanner.addExcludeFilter(new AnnotationTypeFilter(Controller.class));
            scanner.addExcludeFilter(new AnnotationTypeFilter(Component.class));
            for (String pkg : basePkgs) {
                for (BeanDefinition bd : scanner.findCandidateComponents(pkg)) {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    generateInventoryDetails(folderName, clazz);
                }
            }
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }
}
