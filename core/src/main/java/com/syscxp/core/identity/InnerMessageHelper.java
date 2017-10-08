package com.syscxp.core.identity;

import com.syscxp.core.CoreGlobalProperty;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import com.syscxp.header.message.APIMessage;
import com.syscxp.utils.gson.JSONObjectUtil;

public class InnerMessageHelper {

    public static void setMD5(APIMessage message) {
        message.setSignature(getMD5(message));
    }

    public static String getMD5(APIMessage message) {
        return DigestUtils.md5Hex(JSONObjectUtil.toJsonString(message.getDeclaredFieldAndValues())
                + CoreGlobalProperty.INNER_MESSAGE_MD5_KEY);
    }

    public static Boolean validSignature(APIMessage message) {
        if (StringUtils.isEmpty(message.getSignature()))
            return false;
        String signature = message.getSignature();
        message.setSignature(null);
        return signature.equals(getMD5(message)) && !isExpirate(message);
    }

    public static Boolean isExpirate(APIMessage message) {
        return System.currentTimeMillis() - message.getCreatedTime() > CoreGlobalProperty.INNER_MESSAGE_EXPIRE * 1000;
    }
}
