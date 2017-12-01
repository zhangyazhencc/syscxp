package com.syscxp.sms;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.sms.header.*;
import com.syscxp.utils.StringDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;


public class ImageCodeServiceImpl extends AbstractService implements ImageCodeService, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ImageCodeServiceImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;

    @Override
    public boolean ValidateImageCode(String imageId, String code) {
        return false;
    }

    private Map<String, String> sessions = new ConcurrentHashMap<>();
    private Future<Void> expiredSessionCollector;

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    public void handleMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }



    public void init() {
        try {
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    public void destroy() {
        logger.debug("mail service destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
    }

    private void startExpiredSessionCollector() {
        logger.debug("start mail session expired session collector");
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
                return "MailExpiredSessionCleanupThread";
            }

        });
    }

    public String getId() {
        return bus.makeLocalServiceId(MailConstant.SERVICE_ID);
    }


    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return null;
    }
}
