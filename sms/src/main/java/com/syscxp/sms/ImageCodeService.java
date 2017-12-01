package com.syscxp.sms;

/**
 * Created by wangwg on 2017/12/01.
 */

public interface ImageCodeService {
    boolean ValidateImageCode(String imageId, String code);
}
