package com.syscxp.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;


public class HMAC {
    private final static String KEY_MAC_DEFAULT = "HmacMD5";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public HMAC() {
    }

    public static String encryptBase64(String key) {
        return Base64.getEncoder().encodeToString(encodeUTF8(key));
    }

    public static String decryptBase64(String key) {
        return decodeUTF8(Base64.getDecoder().decode(key));
    }

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

    public static String encryptHMACString(String data, String key) {
        return encryptHMACString(data, key, KEY_MAC_DEFAULT);
    }

    public static String encryptHMACString(String data, String key, String algorithm) {
        if (data == null || data.length() == 0) {
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
            String stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs.append("0");
            }
            hs.append(stmp);
        }
        return hs.toString();
    }

    public static void main(String[] args) {

        String str = "test";

        System.out.println(HMAC.encryptBase64(str));

        String rsc = "dGVzdA==";

        System.out.println(HMAC.decryptBase64(rsc));

        String secretKey = "MmX4b8ySs5wHrFPTKeFYfUOHB6CeF6";
        String srcStr = "GEThttp://api.syscxp.com/tunnel/v1?Action=QueryInterface&Nonce=12232&q=name=api-test&SecretId=accountqkx0aFFnstS37E0d&Timestamp=1556785768";

        String hmac = HMAC.encryptHMACString(srcStr, secretKey, "HmacMD5");
        String Signature = HMAC.encryptBase64(hmac);

        System.out.println(Signature);
    }
}
