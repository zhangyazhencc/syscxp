package com.syscxp.core.identity;

import com.syscxp.core.CoreGlobalProperty;
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
public class RedisSession {

    protected static final CLogger logger = Utils.getLogger(AbstractIdentityInterceptor.class);

    @Autowired
    protected RedisTemplate redisTemplate;

    public void put(String sessionUuid, SessionInventory session) {
        if (sessionUuid != null && session != null){
            redisTemplate.opsForValue().set(sessionUuid, JSONObjectUtil.toJsonString(session), 7200, TimeUnit.SECONDS);
        }
    }

    public void put(String sessionUuid, SessionInventory session, int interval, TimeUnit unit) {
        if (sessionUuid != null && session != null){
            redisTemplate.opsForValue().set(sessionUuid, JSONObjectUtil.toJsonString(session), interval, unit);
        }
    }

    public SessionInventory get(String sessionUuid){
        SessionInventory  session = JSONObjectUtil.toObject((String)redisTemplate.opsForValue().get(sessionUuid), SessionInventory.class);

        logger.trace("redis session: " + JSONObjectUtil.toJsonString(session));

        return session;
    }

    public void remove(String sessionUuid){
        redisTemplate.delete(sessionUuid);
    }
}
