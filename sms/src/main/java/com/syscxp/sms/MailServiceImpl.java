package com.syscxp.sms;

import com.syscxp.sms.header.APIValidateMailCodeMsg;
import com.syscxp.sms.header.APIValidateMailCodeReply;
import com.syscxp.sms.header.*;
import com.syscxp.utils.StringDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.exception.CloudRuntimeException;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;


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
        boolean isValidate;
    }

    private Map<String, VerificationCode> sessions = new ConcurrentHashMap<>();
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
        logger.debug("mail service destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
        return true;
    }

    public void handleMessage(Message msg) {

        if (msg instanceof APIMailCodeSendMsg) {
            handle((APIMailCodeSendMsg) msg);
        }else if (msg instanceof APIValidateMailCodeMsg) {
            handle((APIValidateMailCodeMsg) msg);
        }else if(msg instanceof APISendMailMsg){
            handle((APISendMailMsg) msg);
        }
        else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APISendMailMsg msg) {
        String[] emails = msg.getEmails().toArray(new String[msg.getEmails().size()]);

        mailSend(emails,msg.getSubject(),msg.getContent());

        APIMaiAlarmSendEvent evt = new APIMaiAlarmSendEvent(msg.getId());

        bus.publish(evt);
    }

    private void  handle(APIValidateMailCodeMsg msg){
        APIValidateMailCodeReply reply = new APIValidateMailCodeReply();

        boolean valid = false;
        VerificationCode verificationCode = sessions.get(msg.getMail());
        if (verificationCode == null){
        }else{
            Timestamp curr = new Timestamp(System.currentTimeMillis());
            if (!verificationCode.isValidate && curr.before(verificationCode.expiredDate)
                    && msg.getCode().equals(verificationCode.code)) {
                valid = true;
            }else{
            }
        }
        reply.setValid(valid);
        if(valid){
            verificationCode.isValidate = true;
        }
        sessions.put(msg.getMail(),verificationCode);
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

        String code = StringDSL.getRandomNumbersString(10);
        boolean result = mailSend(msg.getMail(),"验证码", code);
        if(result){
            VerificationCode verificationCode = sessions.get(msg.getMail());
            long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60 * 10);   // 10 minute
            if (verificationCode == null){
                VerificationCode vcode = new VerificationCode();
                vcode.code = code;
                vcode.expiredDate = new Timestamp(expiredTime);
                vcode.isValidate = false;
                sessions.put(msg.getMail(), vcode);
            }else{
                verificationCode.code = code;
                verificationCode.isValidate = false;
                verificationCode.expiredDate = new Timestamp(expiredTime);
            }
        }

        APIMailCodeSendReply reply = new APIMailCodeSendReply();
        bus.reply(msg, reply);
    }

    public boolean mailSend(String mail, String subject, String content){
        return mailSend(new String[]{mail}, subject, content);
    }

    public boolean mailSend(String[] mails, String subject, String content){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mails);
        mailMessage.setFrom(MailGlobalProperty.FROM);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
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

        return true;
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

        }, 50);
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

    public void sendAlarmMonitorMsg(List<String> email,String subject, String content){
        APISendMailMsg msg = new APISendMailMsg();
        msg.setEmails(email);
        msg.setSubject(subject);
        msg.setContent(content);
        msg.setServiceId(bus.makeLocalServiceId(MailConstant.SERVICE_ID));

        bus.send(msg);
    }
}
