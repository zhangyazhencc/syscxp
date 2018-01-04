package com.syscxp.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;


/**
 * Project: syscxp
 * Package: com.syscxp.utils
 * Date: 2018/1/3 8:39
 * Author: wj
 */
public class HMAC {
    /**
     * 定义加密方式
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    private final static String KEY_MAC_DEFAULT = "HmacMD5";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public HMAC() {
    }

    /**
     * BASE64 加密
     *
     * @param key 需要加密的字符串
     * @return 字符串
     */
    public static String encryptBase64(String key) {
        return Base64.encodeBase64String(encodeUTF8(key));
    }

    /**
     * BASE64 解密
     *
     * @param key 需要解密的字符串
     * @return 字节数组
     */
    public static String decryptBase64(String key) {
        return decodeUTF8(Base64.decodeBase64(key));
    }

    /**
     * HMAC加密
     *
     * @param data 需要加密的字节数组
     * @param key  密钥
     * @return 字节数组
     */
    public static byte[] encryptHMAC(byte[] data, String key) {
        return encryptHMAC(data, key, KEY_MAC_DEFAULT);
    }

    public static byte[] encryptHMAC(byte[] data, String key, String algorithm) {
        SecretKey secretKey;
        byte[] bytes = null;
        try {
            byte[] srcBytes = encodeUTF8(key);
            secretKey = new SecretKeySpec(srcBytes, algorithm);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * HMAC加密
     *
     * @param data 需要加密的字符串
     * @param key  密钥
     * @return 字符串
     */
    public static String encryptHMACString(String data, String key) {
        return encryptHMACString(data, key, KEY_MAC_DEFAULT);
    }

    public static String encryptHMACString(String data, String key, String algorithm) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] bytes = encryptHMAC(data.getBytes(), key, algorithm);
        return byte2hex(bytes);
    }


    private static String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    private static String byte2hex(final byte[] b) {
        StringBuilder hs = new StringBuilder();
        for (int n = 0; n < b.length; n++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式。
            String stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs.append("0");
            }
            hs.append(stmp);
        }
        return hs.toString();
    }
}
