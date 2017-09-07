package org.zstack.core.identity;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.header.message.InnerAPIMessage;
import org.zstack.header.rest.RESTApiFacade;
import org.zstack.utils.gson.JSONObjectUtil;

public class InnerMessageHelper {

    private static final String KEY_ = CoreGlobalProperty.INNER_MESSAGE_MD5_KEY;

    private static final long EXPIRATION = CoreGlobalProperty.INNER_MESSAGE_EXPIRE;

    public static void setMD5(InnerAPIMessage message) {
        message.setSignature(getMD5(message));
    }

    public static String getMD5(InnerAPIMessage message) {
        return DigestUtils.md5Hex(JSONObjectUtil.toJsonString(message.getDeclaredFieldAndValues()) + KEY_);
    }

    public static Boolean validSignature(InnerAPIMessage message) {
        if (StringUtils.isEmpty(message))
            return false;
        String signature = message.getSignature();
        message.setSignature(null);
        return signature.equals(getMD5(message)) && !isExpirate(message);
    }

    public static Boolean isExpirate(InnerAPIMessage message) {
        return System.currentTimeMillis() - message.getCreatedTime() > EXPIRATION * 1000;
    }
}
