package com.syscxp.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by frank on 3/3/2016.
 */
public class Digest {


    public static String getMD5ByFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(String str) {
        return getSignature(MessageDigestAlgorithms.MD5, str);
    }

    public static String getSHA1(String str) {
        return getSignature(MessageDigestAlgorithms.SHA_1, str);
    }

    public static String getSHA256(String str) {
        return getSignature(MessageDigestAlgorithms.SHA_256, str);
    }

    public static String getSignature(String signatureMethod, String str) {
        try {
            if (MessageDigestAlgorithms.MD5.equals(signatureMethod)) {
                return DigestUtils.md5Hex(str);
            } else if (MessageDigestAlgorithms.SHA_1.equals(signatureMethod)) {
                return DigestUtils.sha1Hex(str);
            } else if (MessageDigestAlgorithms.SHA_256.equals(signatureMethod)) {
                return DigestUtils.sha256Hex(str);
            } else {
                throw new RuntimeException("No such signature method!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
