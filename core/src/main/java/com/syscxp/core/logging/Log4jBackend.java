package com.syscxp.core.logging;

import org.apache.commons.lang.LocaleUtils;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;

import java.util.Locale;

/**
 * Created by xing5 on 2016/5/30.
 */
public class Log4jBackend implements LogBackend {
    private static final CLogger logger = Utils.getLogger(Log4jBackend.class);

    @Override
    public void writeLog(Log log) {
        if (LogGlobalProperty.LOG4j_BACKEND_ON) {
            logger.debug(JSONObjectUtil.toJsonString(log.getContent()));
        }
    }

    @Override
    public Locale getCurrentLocale() {
        return LocaleUtils.toLocale(CoreGlobalProperty.LOCALE);
    }
}
