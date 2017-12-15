package com.syscxp.sms;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.sms.header.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangwg on 2017/12/01.
 */
public class ImageCodeServiceImpl extends AbstractService implements ImageCodeService, ApiMessageInterceptor{

    private static final CLogger logger = Utils.getLogger(ImageCodeServiceImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;

    private Map<String, String> sessions = new ConcurrentHashMap<>();

    private Future<Void> expiredSessionCollector;

    public boolean start() {
        try {
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }

        return true;
    }

    public boolean stop() {
        logger.debug("imageCode service destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
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

        if(sessions.get(msg.getUuid()) != null && sessions.get(msg.getUuid()).equals(msg.getCode())){
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
        sessions.put(map.get("uuid"),map.get("randomString"));
        bus.reply(msg, reply);

    }

    @Override
    public boolean ValidateImageCode(String imageUuid, String imageCode) {
        if(sessions.get(imageUuid) != null && sessions.get(imageUuid).equals(imageCode)){
            return true;
        }

        return false;
    }

    private void startExpiredSessionCollector() {
        logger.debug("start imageCode session expired session collector");
        expiredSessionCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            @Override
            public void run() {
                sessions.clear();
            }

            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override
            public long getInterval() {
                return 60 * 30; // 30 minute
            }

            @Override
            public String getName() {
                return "ImageCodeExpiredSessionCleanupThread";
            }

        }, 40);
    }

    public String getId() {
        return bus.makeLocalServiceId(ImageCodeConstant.SERVICE_ID);
    }


    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }



}
