package org.zstack.sms;

import com.cloopen.rest.sdk.CCPRestSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.sms.header.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.zstack.core.Platform.argerr;

/**
 * Created by zxhread on 17/8/14.
 */
public class SmsServiceImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SmsServiceImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    public void handleMessage(Message msg) {

        if (msg instanceof APISendSmsMsg) {
            handle((APISendSmsMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APISendSmsMsg msg){

        //初始化短信接口平台
        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(SmsGlobalProperty.SMS_YUNTONGXUN_SERVER_URL, SmsGlobalProperty.SMS_YUNTONGXUN_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_SID, SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(SmsGlobalProperty.SMS_YUNTONGXUN_APPID);// 初始化应用ID

        //调用发送短信接口
        HashMap<String, Object> result = null;
        result = restAPI.sendTemplateSMS(msg.getPhone(),msg.getTemplateId(), msg.getData().toArray(new String[msg.getData().size()]));
        logger.debug("SDKTestSendTemplateSMS result=" + result);

        //返回结果信息
        String statusCode = result.get("statusCode").toString();	//返回状态码
        String dateCreated = null;									//短信发送成功后的创建日期
        String smsMessageSid = null;								//短信发送成功后的唯一标识码
        String statusMsg = null;									//短信发送的状态信息

        long createDate = System.currentTimeMillis();				//该系统时间为短信请求时间戳
        Date createDay = new Date();								//该系统时间为短信请求日期


        //通过返回结果处理
        if("000000".equals(result.get("statusCode"))){	//正常返回

            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            ArrayList TemplateSMS = (ArrayList)data.get("TemplateSMS");
            HashMap<String,Object> data2 = (HashMap<String, Object>) TemplateSMS.get(0);
            smsMessageSid = data2.get("smsMessageSid").toString();
            dateCreated = data2.get("dateCreated").toString();
            statusMsg = "短信发送成功";

        }else{											//异常返回输出错误码和错误信息
            statusMsg = result.get("statusMsg").toString();
        }

        SmsVO sms = new SmsVO();
        sms.setStatusCode(statusCode);
        sms.setDateCreated(dateCreated);
        sms.setSmsMessagesId(smsMessageSid);
        sms.setStatusMsg(statusMsg);
        sms.setCreateDay(createDay);

        dbf.persistAndRefresh(sms);

        APISendSmsEvent evt = new APISendSmsEvent(msg.getId());
        evt.setInventory(SmsInventory.valueOf(sms));

        bus.publish(evt);
    }

    public String getId() {
        return bus.makeLocalServiceId(SmsConstant.SERVICE_ID);
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APISendSmsMsg) {
            validate((APISendSmsMsg) msg);
        }
        return msg;
    }

    private void validate(APISendSmsMsg msg) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");	//设置日期格式
        String createDay = df.format(new Date());					// new Date()为获取当前系统时间

        SimpleQuery<SmsVO> q = dbf.createQuery(SmsVO.class);
        q.add(SmsVO_.phone, SimpleQuery.Op.EQ, msg.getPhone());
        q.add(SmsVO_.createDate, SimpleQuery.Op.GTE, createDay);
        long total = q.count();
        if (total > Long.valueOf(SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_PHONE).longValue()){
            throw new ApiMessageInterceptionException(argerr("same phone not send more than %s messages per day : %s",
                    SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_PHONE, msg.getPhone()));
        }

        q = dbf.createQuery(SmsVO.class);
        q.add(SmsVO_.ip, SimpleQuery.Op.EQ, msg.getIp());
        q.add(SmsVO_.createDate, SimpleQuery.Op.GTE, createDay);
        total = q.count();

        if (total > Long.valueOf(SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP).longValue()){
            throw new ApiMessageInterceptionException(argerr("same Ip not send more than %s messages per day : %s",
                    SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP, msg.getPhone()));
        }
    }
}
