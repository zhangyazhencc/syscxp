package com.syscxp.core.aspect;

import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.encrypt.EncryptRSA;

public aspect EncryptAspect {
    private static final CLogger logger = Utils.getLogger(EncryptAspect.class);

    @Autowired
    private EncryptRSA rsa;

    void around(String param) : args(param) && execution(@com.syscxp.header.core.encrypt.ENCRYPT * *(..)){
        if(param.length() > 0){
            try{
                param = rsa.encrypt1(param);
            }catch(Exception e){
                logger.debug(String.format("encrypt aspectj is error..."));
                logger.debug(e.getMessage());
                e.printStackTrace();
            }
            proceed(param);
        }
    }


}