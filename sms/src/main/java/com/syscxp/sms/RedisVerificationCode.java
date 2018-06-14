package com.syscxp.sms;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class RedisVerificationCode implements VerificationCode {

    protected static final CLogger logger = Utils.getLogger(RedisVerificationCode.class);

    @Autowired
    protected RedisTemplate redisTemplate;

    private String prefix = "vfc";
    @Override
    public void put(String uuid, String code) {
        if (uuid != null && code != null){
            uuid = prefix + uuid;
            redisTemplate.opsForValue().set(uuid, code, 600, TimeUnit.SECONDS);
        }
    }

    public void put(String uuid, String code, int interval, TimeUnit unit) {
        if (uuid != null && code != null){
            uuid = prefix + uuid;
            redisTemplate.opsForValue().set(uuid, code, interval, unit);
        }
    }

    @Override
    public String get(String uuid){
        if (uuid == null){
            return null;
        }else {
            uuid = prefix + uuid;
            return (String) redisTemplate.opsForValue().get(uuid);
        }
    }

    @Override
    public void remove(String uuid){
        redisTemplate.delete(uuid);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
