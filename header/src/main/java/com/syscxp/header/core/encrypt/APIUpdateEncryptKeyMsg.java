package com.syscxp.header.core.encrypt;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

/**
 * Created by mingjian.deng on 16/12/28.
 */
public class APIUpdateEncryptKeyMsg extends APIMessage {

    @APIParam
    String encryptKey;

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }
 
    public static APIUpdateEncryptKeyMsg __example__() {
        APIUpdateEncryptKeyMsg msg = new APIUpdateEncryptKeyMsg();


        return msg;
    }

}
