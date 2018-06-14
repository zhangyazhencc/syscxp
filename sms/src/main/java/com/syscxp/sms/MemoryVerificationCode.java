package com.syscxp.sms;

import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.core.RedisTemplate;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class MemoryVerificationCode implements VerificationCode {

    protected static final CLogger logger = Utils.getLogger(MemoryVerificationCode.class);

    @Autowired
    private ThreadFacade thdf;

    class VCode {
        String code;
        Timestamp expiredDate;
    }

    private Map<String, VCode> sessions = new ConcurrentHashMap<>();

    private Future<Void> expiredSessionCollector;

    public void put(String uuid, String code) {
        if (uuid != null && code != null){
            VCode vcode = new VCode();
            vcode.code = code;
            vcode.expiredDate = new Timestamp(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60 * 10));
            sessions.put(uuid, vcode);
        }
    }

    public String get(String uuid){
        VCode vcode = sessions.get(uuid);

        if (vcode == null){
            return null;
        }else{
            Timestamp curr = new Timestamp(System.currentTimeMillis());
            if (curr.before(vcode.expiredDate)){
                return vcode.code;
            }else{
                return null;
            }
        }
    }

    public void remove(String uuid){
        sessions.remove(uuid);
    }

    private void startExpiredSessionCollector() {
        logger.debug("start VerificationCode expired collector");
        expiredSessionCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            @Override
            public void run() {
                Timestamp curr = new Timestamp(System.currentTimeMillis());
                for (Map.Entry<String, VCode> entry : sessions.entrySet()) {
                    VCode v = entry.getValue();
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
                return "MemoryVerificationCodeCleanupThread";
            }

        }, 60 * 30);
    }

    public void start() {
        try {
            startExpiredSessionCollector();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    public void stop() {
        logger.debug("MemoryVerificationCode destroy.");
        if (expiredSessionCollector != null) {
            expiredSessionCollector.cancel(true);
        }
    }
}
