package com.syscxp.core.identity;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import com.syscxp.header.message.APIMessage;
import com.syscxp.utils.gson.JSONObjectUtil;

public class InnerMessageHelper {
    private static final CLogger logger = Utils.getLogger(AbstractIdentityInterceptor.class);

    public static void setMD5(APIMessage message) {
        message.setSignature(getMD5(message));
    }

    public static String getMD5(APIMessage message) {
        return getMD5(message, CoreGlobalProperty.INNER_MESSAGE_MD5_KEY);
    }

    public static String getMD5(APIMessage message, String key) {
        String str = JSONObjectUtil.toJsonString(message.getDeclaredFieldAndValues()) + key;
        logger.trace(String.format("signature info [%s]", str));
        return DigestUtils.md5Hex(JSONObjectUtil.toJsonString(message.getDeclaredFieldAndValues()) + key);
    }

    public static Boolean validSignature(APIMessage message) {
        return validSignature(message, CoreGlobalProperty.INNER_MESSAGE_MD5_KEY);
    }

    public static Boolean validSignature(APIMessage message, String key) {
        if (StringUtils.isEmpty(message.getSignature()))
            return false;
        String signature = message.getSignature();
        message.setSignature(null);
        String md5 = getMD5(message, key);
        logger.trace(String.format("signature info [received: %s, %s], [local: %s, %s], [%s]", signature, message.getCreatedTime(),
                md5, System.currentTimeMillis(), key));

        return signature.equals(md5) && ! isExpirate(message);
    }


    public static Boolean isExpirate(APIMessage message) {
        return System.currentTimeMillis() - message.getCreatedTime() > CoreGlobalProperty.INNER_MESSAGE_EXPIRE * 1000;
    }
}
