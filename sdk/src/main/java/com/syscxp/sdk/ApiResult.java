package com.syscxp.sdk;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:04
 * Author: wj
 */
public class ApiResult {
    public String code;
    public String message;
    private String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    void setResult(String result) {
        this.result = result;
    }

    private static Object getProperty(Object bean, Iterator<String> it) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String path = it.next();
        if (bean instanceof Map) {
            Pattern re = Pattern.compile("(.*)\\[(\\d+)]");
            Matcher m = re.matcher(path);
            if (m.find()) {
                path = String.format("(%s)[%s]", m.group(1), m.group(2));
            }
        }

        Object val = PropertyUtils.getProperty(bean, path);

        if (it.hasNext()) {
            return getProperty(val, it);
        } else {
            return val;
        }
    }

    public static Object getProperty(Object bean, String path) {
        List<String> paths = asList(path.split("\\."));
        try {
            return getProperty(bean, paths.iterator());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setProperty(Object bean, Iterator<String> it, String fieldName, Object val) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (it.hasNext()) {
            bean = getProperty(bean, it);
        }

        if (bean instanceof Map) {
            Pattern re = Pattern.compile("(.*)\\[(\\d+)]");
            Matcher m = re.matcher(fieldName);
            if (m.find()) {
                fieldName = String.format("(%s)[%s]", m.group(1), m.group(2));
            }
        }

        PropertyUtils.setProperty(bean, fieldName, val);
    }

    public static void setProperty(Object bean, String path, Object val) {
        List<String> paths = asList(path.split("\\."));
        String fieldName = paths.get(paths.size()-1);
        paths = paths.subList(0, paths.size()-1);

        try {
            setProperty(bean, paths.iterator(), fieldName, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T> T getResult(Class<T> clz) {
        if (result == null || result.isEmpty()) {
            return null;
        }

        Map m = ZSClient.gson.fromJson(result, LinkedHashMap.class);
        T ret = ZSClient.gson.fromJson(result, clz);
        if (!m.containsKey("schema")) {
            return ret;
        }

        Map<String, String> schema = (Map) m.get("schema");
        try {
            for (String path : schema.keySet()) {
                String src = schema.get(path);
                String dst = SourceClassMap.srcToDstMapping.get(src);

                if (dst == null) {
                    //TODO: warning
                    continue;
                }

                Object bean = getProperty(ret, path);
                if (bean.getClass().getName().equals(dst)) {
                    // not an inherent object
                    continue;
                }

                Class dstClz = Class.forName(dst);
                Object source = getProperty(m, path);
                Object dstBean = ZSClient.gson.fromJson(ZSClient.gson.toJson(source), dstClz);
                setProperty(ret, path, dstBean);
            }

            return ret;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }
}
