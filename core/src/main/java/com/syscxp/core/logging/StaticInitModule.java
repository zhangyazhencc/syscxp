package com.syscxp.core.logging;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import com.syscxp.core.Platform;
import com.syscxp.core.StaticInit;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by xing5 on 2016/6/1.
 */
public class StaticInitModule {
    private static final CLogger logger = Utils.getLogger(StaticInitModule.class);

    @StaticInit
    public static void init() throws IllegalAccessException, IOException {
        Reflections reflections = Platform.getReflections();

        Map<String, Map<String, String>> localeMsgs = new HashMap<>();

        Set<Field> fields = reflections.getFieldsAnnotatedWith(LogLabel.class);
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                throw new CloudRuntimeException(String.format("%s:%s annotated by @LogLabel is not a static field",
                        f.getDeclaringClass(), f.getName()));
            }

            LogLabel lab = f.getAnnotation(LogLabel.class);
            String labelName = (String) f.get(null);

            for (String msg : lab.messages()) {
                String[] pair = msg.split("=", 2);
                if (pair.length != 2) {
                    throw new CloudRuntimeException(String.format("invalid label[%s]. the label must be in format of 'locale = message body'", msg));
                }

                String l = pair[0];
                l = l.trim();
                Locale locale;
                try {
                    locale = LocaleUtils.toLocale(l);
                } catch (IllegalArgumentException e) {
                    throw new CloudRuntimeException(String.format("invalid locale[%s] in the label[%s]", l, msg), e);
                }

                Map<String, String> labels = localeMsgs.computeIfAbsent(locale.toString(), k -> new LinkedHashMap<>());

                if (labels.containsKey(labelName)) {
                    throw new CloudRuntimeException(String.format("duplicate label. There has been a label[%s] for locale[%s]",
                            labelName, locale.toString()));
                }

                String msgBody = pair[1];
                msgBody = msgBody.trim();
                labels.put(labelName, msgBody);
            }
        }

        for (Map.Entry<String, Map<String, String>> e : localeMsgs.entrySet()) {
            String locale = e.getKey();
            Map<String, String> labels = e.getValue();
            File msgFile = PathUtil.findFileOnClassPath(String.format("i18n/messages_%s.properties", locale), true);
            List<String> lst = new ArrayList<String>();
            for (Map.Entry<String, String> i : labels.entrySet()) {
                lst.add(String.format("%s = %s", i.getKey(), i.getValue()));
            }

            FileUtils.write(msgFile, StringUtils.join(lst, "\n"), "UTF-8");

            logger.debug(String.format("created i18n message file %s", msgFile.getAbsolutePath()));
        }
    }
}
