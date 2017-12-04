package com.syscxp.sms;

import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;
import com.syscxp.core.Platform;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
        return true;
    }

    public boolean stop() {
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

        Map<String,String> map = getBase64Code();

        reply.setImageUuid(map.get("uuid"));
        reply.setImageCode(map.get("base64Code"));
        bus.reply(msg, reply);

    }

    @Override
    public boolean ValidateImageCode(String imageId, String code) {
        return false;
    }

    public void init() {
        try {
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    public void destroy() {
        logger.debug("imageCode service destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
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

        });
    }

    public String getId() {
        return bus.makeLocalServiceId(ImageCodeConstant.SERVICE_ID);
    }


    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return null;
    }

    private Random random = new Random();
    private String randString = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int width = 80;
    private int height = 23;
    private int lineSize = 40;
    private int stringNum = 4;

    private Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    private String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString
                .length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
    public Map<String ,String> getBase64Code() {
        BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drowString(g, randomString, i);
        }
        g.dispose();
        byte[] data ;
        String base64Code="";
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", tmp);
            data = tmp.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            base64Code = encoder.encode(data);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                tmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String ,String> map = new HashMap<>();
        map.put("uuid",Platform.getUuid());
        map.put("base64Code",base64Code);
        sessions.put(map.get("uuid"),randomString);
        return map;
    }

}
