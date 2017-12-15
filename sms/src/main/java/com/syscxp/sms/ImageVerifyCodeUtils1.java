package com.syscxp.sms;

import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;
import com.syscxp.core.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ImageVerifyCodeUtils1 {

    private Random random = new Random();
    private String randString = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
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

    private String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    public Map<String ,String> getBase64Code() {
        BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        g.setColor(getRandColor(110, 133));

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
        map.put("uuid", Platform.getUuid());
        map.put("base64Code",base64Code);
        map.put("randomString",randomString);
        return map;
    }
}
