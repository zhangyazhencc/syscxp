package org.zstack.sms;

import com.cloopen.rest.sdk.CCPRestSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.thread.PeriodicTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.identity.SessionPolicyInventory;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.Message;
import org.zstack.sms.header.*;
import org.zstack.utils.StringDSL;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

        if (msg instanceof APISendSmsMsg) {
            handle((APISendSmsMsg) msg);
        }else if (msg instanceof APIGetVerificationCodeMsg) {
            handle((APIGetVerificationCodeMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void  handle(APIGetVerificationCodeMsg msg){
        String code = StringDSL.getRandomNumbersString(4);
        SmsVO sms = sendMsg(msg.getPhone(), SmsGlobalProperty.SMS_VERIFICATION_CODE_APPID, SmsGlobalProperty.SMS_VERIFICATION_CODE_TEMPLATEID
                , new String[]{code, "10"});

        VerificationCode verificationCode = sessions.get(msg.getPhone());
        if (verificationCode == null){
            long expiredTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60 * 10);   // 10 minute
            VerificationCode vcode = new VerificationCode();
            vcode.code = code;
            vcode.expiredDate = new Timestamp(expiredTime);

            sessions.put(msg.getPhone(), vcode);
        }
        APIGetVerificationCodeReply reply = new APIGetVerificationCodeReply();
        bus.reply(msg, reply);
    }

    private void handle(APISendSmsMsg msg){

        SmsVO sms = sendMsg(msg.getPhone(), SmsGlobalProperty.SMS_YUNTONGXUN_APPID, msg.getTemplateId(), msg.getData().toArray(new String[msg.getData().size()]));

        APISendSmsEvent evt = new APISendSmsEvent(msg.getId());
        evt.setInventory(SmsInventory.valueOf(sms));

        bus.publish(evt);
    }

    private SmsVO sendMsg(String phone, String appId, String templateId, String[] datas){
        //初始化短信接口平台
        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init(SmsGlobalProperty.SMS_YUNTONGXUN_SERVER_URL, SmsGlobalProperty.SMS_YUNTONGXUN_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_SID, SmsGlobalProperty.SMS_YUNTONGXUN_ACCOUNT_TOKEN);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(appId);// 初始化应用ID

        //调用发送短信接口
        HashMap<String, Object> result = null;
        result = restAPI.sendTemplateSMS(phone, templateId, datas);
        logger.debug("SDKTestSendTemplateSMS result=" + result);


        SmsVO sms = new SmsVO();
        sms.setStatusCode(result.get("statusCode").toString()); //返回状态码

        Date createDay = new Date();								//该系统时间为短信请求日期
        sms.setCreateDay(createDay);
        //通过返回结果处理
        if("000000".equals(result.get("statusCode"))){	//正常返回

            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            ArrayList TemplateSMS = (ArrayList)data.get("TemplateSMS");
            HashMap<String,Object> data2 = (HashMap<String, Object>) TemplateSMS.get(0);

            sms.setDateCreated(data2.get("dateCreated").toString());        //短信发送成功后的创建日期
            sms.setSmsMessagesId(data2.get("smsMessageSid").toString());  //短信发送成功后的唯一标识码
        }
        sms.setStatusMsg(result.get("statusMsg").toString());

        dbf.persistAndRefresh(sms);

        return sms;
    }

    public void init() {
        try {
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }
    public void destroy() {
        logger.debug("sms service destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
    }
    private void startExpiredSessionCollector() {
        logger.debug("start sms session expired session collector");
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
                return "SmsExpiredSessionCleanupThread";
            }

        });
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

        if (msg.getId() != null ) {
            q = dbf.createQuery(SmsVO.class);
            q.add(SmsVO_.ip, SimpleQuery.Op.EQ, msg.getIp());
            q.add(SmsVO_.createDate, SimpleQuery.Op.GTE, createDay);
            total = q.count();

            if (total > Long.valueOf(SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP).longValue()) {
                throw new ApiMessageInterceptionException(argerr("same Ip not send more than %s messages per day : %s",
                        SmsGlobalProperty.TOTAL_LIMIT_PER_DAY_IP, msg.getPhone()));
            }
        }
    }
}
