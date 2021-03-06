package com.syscxp.sms;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.sms.header.*;
import com.syscxp.utils.StringDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.data.StringTemplate;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Timestamp;
import java.util.*;

import static com.syscxp.core.Platform.argerr;

/**
 * Created by zxhread on 17/8/14.
 */
public class SmsServiceImpl extends AbstractService implements SmsService, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SmsServiceImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private VerificationCode verificationCode;

    public boolean start() {
        verificationCode.start();
        return true;
    }

    public boolean stop() {
        logger.debug("sms service destroy.");
        verificationCode.stop();
        return true;
    }

    public void handleMessage(Message msg) {

        if (msg instanceof APISendSmsMsg) {
            handle((APISendSmsMsg) msg);
        }else if (msg instanceof APIGetVerificationCodeMsg) {
            handle((APIGetVerificationCodeMsg) msg);
        }else if (msg instanceof APIValidateVerificationCodeMsg) {
            handle((APIValidateVerificationCodeMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Override
    public boolean validateVerificationCode(String phone, String code) {
        String vcode = verificationCode.get(phone);
        if (vcode != null && vcode.equalsIgnoreCase(code)){
            return true;
        }else{
            return false;
        }
    }

    public boolean msgValidateCode(String phone, String code) {
        String vcode = verificationCode.get(phone);
        if (vcode != null && vcode.equalsIgnoreCase(code)){
            return true;
        }else{
            return false;
        }
    }

    private void  handle(APIValidateVerificationCodeMsg msg){
        APIValidateVerificationCodeReply reply = new APIValidateVerificationCodeReply();
        reply.setValid(msgValidateCode(msg.getPhone(), msg.getCode()));

        bus.reply(msg, reply);
    }

    private void  handle(APIGetVerificationCodeMsg msg){
        String code = StringDSL.getRandomNumbersString(6);
        SmsVO sms = sendMsg(msg.getSession(), msg.getPhone(), SmsGlobalProperty.SMS_VERIFICATION_CODE_APPID, SmsGlobalProperty.SMS_VERIFICATION_CODE_TEMPLATEID
                , new String[]{code, "10"}, msg.getIp());

        logger.debug("发送验证码：" + sms.getStatusCode()+sms.getStatusMsg() +
                "[手机号码:"+msg.getPhone()+",验证码:"+code+"]");

        verificationCode.put(msg.getPhone(), code);

        APIGetVerificationCodeReply reply = new APIGetVerificationCodeReply();
        bus.reply(msg, reply);
    }


    public void sendAlarmMonitorMsg(List<String> phones, List<String> datas){
        APISendSmsMsg msg = new APISendSmsMsg();
        msg.setPhone(phones);
        msg.setAppId(SmsGlobalProperty.ALARM_APPID);
        msg.setTemplateId(SmsGlobalProperty.SMS_AlARM_TEMPLATEID);
        msg.setData(datas);
        msg.setServiceId(bus.makeLocalServiceId(SmsConstant.SERVICE_ID));

        bus.send(msg);
    }

    private void handle(APISendSmsMsg msg){

        String phones = StringTemplate.join(msg.getPhone(), ",");
        String appid;
        if(msg.getAppId() == null){
            appid = SmsGlobalProperty.SMS_YUNTONGXUN_APPID;
        }else{
            appid = msg.getAppId();
        }

        SmsVO sms = sendMsg(msg.getSession(), phones, appid, msg.getTemplateId(), msg.getData().toArray(new String[msg.getData().size()]), msg.getIp());

        APISendSmsEvent evt = new APISendSmsEvent(msg.getId());
        evt.setInventory(SmsInventory.valueOf(sms));

        bus.publish(evt);
    }

    /**
     * 发送短信模板请求
     * @param phone 必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
     */

    @Override
    public SmsVO sendMsg(SessionInventory session, String phone, String appId, String templateId, String[] datas, String ip){
        //初始化短信接口平台
        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(SmsGlobalProperty.SMS_YUNTONGXUN_SERVER, SmsGlobalProperty.SMS_YUNTONGXUN_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_SID, SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(appId);// 初始化应用ID

        //调用发送短信接口
        HashMap<String, Object> result = null;
        result = restAPI.sendTemplateSMS(phone, templateId, datas);
        logger.debug("SDKTestSendTemplateSMS result=" + result);


        SmsVO sms = new SmsVO();
        sms.setStatusCode(result.get("statusCode").toString()); //返回状态码
        if (session != null){
            sms.setAccountUuid(session.getAccountUuid());
            sms.setUserUuid(session.getUserUuid());
        }

        sms.setIp(ip);
        sms.setPhone(phone);
        sms.setAppId(appId);
        sms.setTemplateId(templateId);
        sms.setData(datas.toString());

        //通过返回结果处理
        if("000000".equals(result.get("statusCode"))){	//正常返回

            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            ArrayList TemplateSMS = (ArrayList)data.get("TemplateSMS");
            HashMap<String,Object> data2 = (HashMap<String, Object>) TemplateSMS.get(0);

            sms.setDateCreated(data2.get("dateCreated").toString());      //短信发送成功后的创建日期
            sms.setSmsMessagesId(data2.get("smsMessageSid").toString());  //短信发送成功后的唯一标识码
        }
        if (result.get("statusMsg") != null) {
            sms.setStatusMsg(result.get("statusMsg").toString());
        }

        dbf.persistAndRefresh(sms);

        return sms;
    }


    public String getId() {
        return bus.makeLocalServiceId(SmsConstant.SERVICE_ID);
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIGetVerificationCodeMsg) {
            validate((APIGetVerificationCodeMsg) msg);
        }
        return msg;
    }

    private void validate(APIGetVerificationCodeMsg msg) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Timestamp zero = new Timestamp(calendar.getTimeInMillis());

        SimpleQuery<SmsVO> q = dbf.createQuery(SmsVO.class);
        q.add(SmsVO_.phone, SimpleQuery.Op.EQ, msg.getPhone());
        q.add(SmsVO_.createDate, SimpleQuery.Op.GTE, zero);
        long total = q.count();
        if (total > Long.valueOf(SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_PHONE).longValue()){
            throw new ApiMessageInterceptionException(argerr("same phone not send more than %s messages per day : %s",
                    SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_PHONE, msg.getPhone()));
        }

        if (msg.getId() != null ) {
            q = dbf.createQuery(SmsVO.class);
            q.add(SmsVO_.ip, SimpleQuery.Op.EQ, msg.getIp());
            q.add(SmsVO_.createDate, SimpleQuery.Op.GTE, zero);
            total = q.count();

            if (total > Long.valueOf(SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP).longValue()) {
                throw new ApiMessageInterceptionException(argerr("same Ip not send more than %s messages per day : %s",
                        SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP, msg.getPhone()));
            }
        }
    }
}
