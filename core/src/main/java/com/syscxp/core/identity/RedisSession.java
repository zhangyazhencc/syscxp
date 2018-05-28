package com.syscxp.core.identity;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class RedisSession {

    @Autowired
    protected RedisTemplate redisTemplate;

    public void put(String sessionUuid, SessionInventory session) {
        if (sessionUuid != null && session != null){
            redisTemplate.opsForValue().set(sessionUuid, JSONObjectUtil.toJsonString(session), CoreGlobalProperty.SESSION_CLEANUP_INTERVAL, TimeUnit.SECONDS);
        }
    }

    public SessionInventory get(String sessionUuid){
        return JSONObjectUtil.toObject(redisTemplate.opsForValue().get(sessionUuid).toString(), SessionInventory.class);
    }

    public void remove(String sessionUuid){
        redisTemplate.delete(sessionUuid);
    }
}
