package com.syscxp.sms;


public interface ImageCodeService {
    boolean ValidateImageCode(String mail, String code);
}
