package com.syscxp.core.aspect;

import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.encrypt.EncryptRSA;


public aspect DecryptAspect {

	@Autowired
	private EncryptRSA encryptRSA;
	private static final CLogger logger = Utils.getLogger(DecryptAspect.class);

	Object around(): execution(@com.syscxp.header.core.encrypt.DECRYPT * *(..)){
		Object value = proceed();
		if (value != null){
			try{
				value = encryptRSA.decrypt1((String) value);
			}catch(Exception e){
				logger.debug(String.format("decrypt aspectj is error..., no need decrypt"));
				logger.debug(e.getMessage());
			}
		}
		return value;
	}

}