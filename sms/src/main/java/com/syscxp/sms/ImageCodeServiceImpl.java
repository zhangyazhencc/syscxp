package com.syscxp.sms;

import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;
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

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.syscxp.core.Platform.operr;

/**
 * Created by wangwg on 2017/12/01.
 */
public class ImageCodeServiceImpl extends AbstractService implements ImageCodeService, ApiMessageInterceptor {

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

        bus.dealWithUnknownMessage(msg);
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

    public String getBase64Image() throws ServletException, IOException {

        int width=166,height=43;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandColor(220, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            //g.drawRect(0,0,width-1,height-1);
        g.draw3DRect(0, 0, width - 1, height - 1, true);
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        String sRand = "";
        String s = "23456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 4; i++) {
            char rand = s.charAt(random.nextInt(s.length()));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(String.valueOf(rand), 13 * i + 6, 16);
        }
        g.drawOval(0, 12, 60, 11);
        g.dispose();
        ByteArrayOutputStream output = null;
        try {
            ImageIO.write(image, "JPEG", output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
        }

        byte[] data = output.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return  encoder.encode(data);

    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
