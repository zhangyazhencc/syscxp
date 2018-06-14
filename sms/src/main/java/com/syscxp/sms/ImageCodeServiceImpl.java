package com.syscxp.sms;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.sms.header.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * Created by wangwg on 2017/12/01.
 */
public class ImageCodeServiceImpl extends AbstractService implements ImageCodeService, ApiMessageInterceptor{

    private static final CLogger logger = Utils.getLogger(ImageCodeServiceImpl.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private VerificationCode verificationCode;

    public boolean start() {
        verificationCode.start();

        return true;
    }

    public boolean stop() {
        logger.debug("imageCode service destroy.");
        verificationCode.stop();
        return true;
    }

    public void handleMessage(Message msg) {
        if(msg instanceof APIGetImageCodeMsg){
            handle((APIGetImageCodeMsg) msg);
        } else if(msg instanceof APIValidateImageCodeMsg){
            handle((APIValidateImageCodeMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIValidateImageCodeMsg msg) {
        APIValidateImageCodeReply reply = new APIValidateImageCodeReply();

        String code = verificationCode.get(msg.getUuid());
        if(code != null && code.equalsIgnoreCase(msg.getCode())){
            reply.setValid(true);
        }else {
            reply.setValid(false);
        }
        bus.reply(msg, reply);
    }

    private void handle(APIGetImageCodeMsg msg) {
        APIGetImageCodeReply reply = new APIGetImageCodeReply();


        Map<String,String> map = new ImageVerifyCodeUtils1().getBase64Code();

        reply.setImageUuid(map.get("uuid"));
        reply.setImageCode(map.get("base64Code"));
        verificationCode.put(map.get("uuid"), map.get("randomString"));
        bus.reply(msg, reply);

    }

    @Override
    public boolean ValidateImageCode(String imageUuid, String imageCode) {

        String code = verificationCode.get(imageUuid);
        if(code != null && code.equalsIgnoreCase(imageCode)){
            return true;
        }else{
            return false;
        }
    }

    public String getId() {
        return bus.makeLocalServiceId(ImageCodeConstant.SERVICE_ID);
    }


    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }

}
