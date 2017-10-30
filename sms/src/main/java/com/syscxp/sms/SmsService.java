package com.syscxp.sms;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.sms.header.SmsVO;

/**
 * Created by zxhread on 17/8/16.
 */
public interface SmsService {

    boolean validateVerificationCode(String phone, String code);
    SmsVO sendMsg(SessionInventory session, String phone,
                          String appId, String templateId, String[] datas, String ip);

}
