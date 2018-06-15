package com.syscxp.sms;

import com.syscxp.sms.header.APIValidateMailCodeMsg;
import com.syscxp.sms.header.APIValidateMailCodeReply;
import com.syscxp.sms.header.*;
import com.syscxp.utils.StringDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import java.util.*;

import static com.syscxp.core.Platform.operr;


public class MailServiceImpl extends AbstractService implements MailService, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(MailServiceImpl.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private VerificationCode verificationCode;

    public boolean start() {
        verificationCode.start();
        return true;
    }

    public boolean stop() {
        logger.debug("mail service destroy.");
        verificationCode.stop();
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

        String vcode = verificationCode.get(msg.getMail());

        if (vcode != null && vcode.equalsIgnoreCase(msg.getCode())){
            reply.setValid(true);
        }else{
            reply.setValid(false);
        }

        bus.reply(msg, reply);
    }

    public boolean ValidateMailCode(String mail, String code) {
        String vcode = verificationCode.get(mail);
        if (vcode != null && vcode.equalsIgnoreCase(code)){
            return true;
        }else{
            return false;
        }
    }

    private void  handle(APIMailCodeSendMsg msg) throws OperationFailureException {

        String code = StringDSL.getRandomNumbersString(10);
        String context = String.format("【犀思云】尊敬的用户：您的校验码：%s，工作人员不会索取，请勿泄漏。", code);
        boolean result = mailSend(msg.getMail(),"犀思云验证码", context);
        if(result){
            verificationCode.put(msg.getMail(), code);
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
        mailMessage.setFrom(MailGlobalProperty.MAIL_FROM);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(MailGlobalProperty.MAIL_HOST);
        mailSender.setPort(25);
        mailSender.setUsername(MailGlobalProperty.MAIL_USERNAME);
        mailSender.setPassword(MailGlobalProperty.MAIL_PASSWORD);
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", true);
        prop.put("mail.smtp.timeout", 5000);
        mailSender.setJavaMailProperties(prop);

        try{
            mailSender.send(mailMessage);
        }catch(Exception e){
            throw new OperationFailureException(operr("message:" + e.getMessage()));
        }
        logger.debug(">>>>>>>>>>>>>>>>>>发送成功<<<<<<<<<<<<<<<<<<<<<<");

        return true;
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
