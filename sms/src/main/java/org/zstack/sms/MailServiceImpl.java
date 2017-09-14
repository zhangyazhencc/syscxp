package org.zstack.sms;

import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.exception.CloudRuntimeException;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.sms.header.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.zstack.core.Platform.operr;


public class MailServiceImpl extends AbstractService implements MailService, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(MailServiceImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;


    class VerificationCode {
        String code;
        Timestamp expiredDate;
    }

    private Map<String, VerificationCode> sessions = new ConcurrentHashMap<>();
    private Future<Void> expiredSessionCollector;

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    public void handleMessage(Message msg) {

        if (msg instanceof APIMailCodeSendMsg) {
            handle((APIMailCodeSendMsg) msg);
        }else if (msg instanceof APIValidateMailCodeMsg) {
            handle((APIValidateMailCodeMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void  handle(APIValidateMailCodeMsg msg){
        APIValidateMailCodeReply reply = new APIValidateMailCodeReply();

        boolean valid = false;
        VerificationCode verificationCode = sessions.get(msg.getMail());
        if (verificationCode == null){
        }else{
            Timestamp curr = new Timestamp(System.currentTimeMillis());
            if (curr.before(verificationCode.expiredDate) && msg.getCode().equals(verificationCode.code)) {
                valid = true;
            }else{
            }
        }

        reply.setValid(valid);

        bus.reply(msg, reply);
    }

    public boolean ValidateMailCode(String mail, String code) {
        VerificationCode verificationCode = sessions.get(mail);

        Timestamp curr = new Timestamp(System.currentTimeMillis());
        if(verificationCode != null
                && curr.before(verificationCode.expiredDate)
                && code.equals(verificationCode.code)){
            return true;
        }else{
            return false;
        }

    }


    private void  handle(APIMailCodeSendMsg msg) throws OperationFailureException {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(msg.getMail());
        mailMessage.setFrom(MailGlobalProperty.FROM);
        mailMessage.setSubject("验证码");
        String code = String.valueOf(new Random().nextInt(1000000));
        mailMessage.setText(code);

        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(MailGlobalProperty.HOST);
        senderImpl.setPort(25);
        senderImpl.setUsername(MailGlobalProperty.USERNAME);
        senderImpl.setPassword(MailGlobalProperty.PASSWORD);
        Properties prop = new Properties();
        prop.put(" mail.smtp.auth ", "true");
        prop.put(" mail.smtp.timeout ", "25000");
        senderImpl.setJavaMailProperties(prop);


        try{
            senderImpl.send(mailMessage);

        }catch(Exception e){
            e.printStackTrace();
            throw new OperationFailureException(operr("message:" + e.getMessage()));
        }

        logger.debug(">>>>>>>>>>>>>>>>>>发送成功<<<<<<<<<<<<<<<<<<<<<<");
        VerificationCode verificationCode = sessions.get(msg.getMail());
        long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60 * 10);   // 10 minute
        if (verificationCode == null){
            VerificationCode vcode = new VerificationCode();
            vcode.code = code;
            vcode.expiredDate = new Timestamp(expiredTime);
            sessions.put(msg.getMail(), vcode);
        }else{
            verificationCode.code = code;
            verificationCode.expiredDate = new Timestamp(expiredTime);
            sessions.put(msg.getMail(), verificationCode);
        }

        APIMailCodeSendReply reply = new APIMailCodeSendReply();
        bus.reply(msg, reply);
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
                Timestamp curr = new Timestamp(System.currentTimeMillis());
                for (Map.Entry<String, VerificationCode> entry : sessions.entrySet()) {
                    VerificationCode v = entry.getValue();
                    if (curr.after(v.expiredDate)) {
                        sessions.remove(entry.getKey());
                    }
                }
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

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIGetVerificationCodeMsg) {
            validate((APIGetVerificationCodeMsg) msg);
        }
        return msg;
    }

    private void validate(APIGetVerificationCodeMsg msg) {

    }
}
