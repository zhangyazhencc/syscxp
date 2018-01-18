package com.syscxp.alarm.log;

import com.syscxp.alarm.header.log.HandleAlarmMsg;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.Component;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-15.
 * @Description: .
 */

public class AlarmQueue<T> implements InitializingBean, DisposableBean, Component {
    private static final CLogger logger = Utils.getLogger(AlarmQueue.class);

    @Autowired
    ThreadFacade thf;
    @Autowired
    private CloudBus bus;

    private RedisTemplate redisTemplate;
    private String key;
    private RedisConnectionFactory factory;
    private RedisConnection connection;
    private Lock lock = new ReentrantLock();//基于底层IO阻塞考虑
    private Thread alarmThread;
    private boolean isInit = true;
    private boolean isClosed;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("============== afterPropertiesSet =================");
        factory = redisTemplate.getConnectionFactory();
        connection = RedisConnectionUtils.getConnection(factory);
        alarmThread = new AlarmThread();
        alarmThread.setDaemon(true);
        alarmThread.start();
    }

    class AlarmThread extends Thread {
        @Override
        public void run() {
            try {
                if (isInit) {
                    Thread.sleep(30000);
                    isInit = false;
                }

                logger.info("============== AlarmThread Start =================");
                while (true) {
                    T value = takeFromTail(0);
                    if (value != null) {
                        try {
                            // listener.onMessage(value);
                            HandleAlarmMsg amsg = new HandleAlarmMsg();
                            amsg.setAlarmValue(value.toString());
                            bus.makeTargetServiceIdByResourceUuid(amsg, AlarmConstant.SERVICE_ID_ALARM_LOG, Platform.getUuid());
                            bus.send(amsg);
                        } catch (Exception e) {
                            logger.error(String.format("fail to handle alarm!Alarm content: %s, Error: %s", value.toString(), e.getMessage()));
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(String.format("Alarm thread InterruptedException! Error: %s", e.getMessage()));
            }
        }
    }

    public T takeFromTail(int timeout) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            List<byte[]> results = connection.bRPop(timeout, key.getBytes());
            if (CollectionUtils.isEmpty(results)) {
                return null;
            }
            return (T) redisTemplate.getValueSerializer().deserialize(results.get(1));
        } catch (Exception e) {
            throw new RuntimeException(String.format("fail to take value from queue %s! Error: ", this.key, e.getMessage()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (isClosed) {
            return;
        }
        shutdown();
        RedisConnectionUtils.releaseConnection(connection, factory);
        isClosed = true;
    }

    private void shutdown() {
        AlarmThread.interrupted();
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
