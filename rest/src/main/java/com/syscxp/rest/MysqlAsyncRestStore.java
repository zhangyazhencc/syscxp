package com.syscxp.rest;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Component;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIEvent;
import com.syscxp.utils.ExceptionDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:08
 * Author: wj
 */
public class MysqlAsyncRestStore implements AsyncRestApiStore, Component {
    private static final CLogger logger = Utils.getLogger(MysqlAsyncRestStore.class);

    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private ResourceDestinationMaker destinationMaker;
    @Autowired
    private ThreadFacade thdf;

    // cache 2000 API results
    private Map<String, APIEvent> results = Collections.synchronizedMap(new LRUMap(RestGlobalProperty.MAX_CACHED_API_RESULTS));
    private Future cleanupThread;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private synchronized EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Override
    public void save(RequestData d) {
        AsyncRestVO vo = new AsyncRestVO();
        vo.setUuid(d.apiMessage.getId());
        vo.setRequestData(d.toJson());
        vo.setState(AsyncRestState.processing);
//        dbf.persist(vo);

        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            mgr.persist(vo);
            mgr.flush();
            mgr.refresh(vo);
            tran.commit();
        } catch (Exception e) {
            ExceptionDSL.exceptionSafe(tran::rollback);
            throw new CloudRuntimeException(e);
        } finally {
            ExceptionDSL.exceptionSafe(mgr::close);
        }
    }

    @Override
    public RequestData complete(APIEvent evt) {
        RequestData d = null;

        if (destinationMaker.isManagedByUs(evt.getApiId())) {
            AsyncRestVO vo = find(evt.getApiId());

            if (vo == null) {
                // for cases that directly send API message which we don't
                // have records
                if (logger.isTraceEnabled()) {
                    logger.warn(String.format("cannot find record for the API event %s", JSONObjectUtil.toJsonString(evt)));
                }

                return null;
            }

            update(evt);

            d = RequestData.fromJson(vo.getRequestData());
        }

        if (!CoreGlobalProperty.UNIT_TEST_ON) {
            // don't use the cache for unit test
            // we want to test the database
            results.put(evt.getApiId(), evt);
        }

        return d;
    }

    private boolean update(APIEvent evt) {
        String sql = "update AsyncRestVO r set r.result = :result, r.state = :state where r.uuid = :uuid";
        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            Query query = mgr.createQuery(sql);
            query.setParameter("result", ApiEventResult.toJson(evt));
            query.setParameter("state", AsyncRestState.done);
            query.setParameter("uuid", evt.getApiId());
            int ret = query.executeUpdate();
            tran.commit();
            return ret > 0;
        } catch (Exception ex) {
            tran.rollback();
            throw new CloudRuntimeException(ex);
        } finally {
            mgr.close();
        }
    }

    private AsyncRestVO find(String uuid) {
        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            AsyncRestVO vo = mgr.find(AsyncRestVO.class, uuid);
            tran.commit();
            return vo;
        } catch (Exception e) {
            tran.rollback();
            throw new CloudRuntimeException(e);
        } finally {
            mgr.close();
        }
    }

    @Override
    public AsyncRestQueryResult query(String uuid) {
        AsyncRestQueryResult result = new AsyncRestQueryResult();
        result.setUuid(uuid);

        APIEvent evt = results.get(uuid);
        if (evt != null) {
            result.setState(AsyncRestState.done);
            result.setResult(evt);
            return result;
        }

        AsyncRestVO vo = find(uuid);

        if (vo == null) {
            result.setState(AsyncRestState.expired);
            return result;
        }

        if (vo.getState() != AsyncRestState.done) {
            result.setState(vo.getState());
            return result;
        }

        try {
            result.setState(AsyncRestState.done);
            result.setResult(ApiEventResult.fromJson(vo.getResult()));

            results.put(uuid, result.getResult());
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }

        return result;
    }

    @Override
    public boolean start() {
        startExpiredApiCleanupThread();
        RestGlobalConfig.SCAN_EXPIRED_API_INTERVAL.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                startExpiredApiCleanupThread();
            }
        });

        return true;
    }

    private void startExpiredApiCleanupThread() {
        if (cleanupThread != null) {
            cleanupThread.cancel(true);
        }

        cleanupThread = thdf.submitPeriodicTask(new PeriodicTask() {
            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override
            public long getInterval() {
                return RestGlobalConfig.SCAN_EXPIRED_API_INTERVAL.value(Integer.class).longValue();
            }

            @Override
            public String getName() {
                return "scan-expired-api-records";
            }

            @Override
            public void run() {
                try {
                    cleanup();
                } catch (Throwable t) {
                    logger.warn("unhandled error", t);
                }
            }

            @Transactional
            private void cleanup() {
//                String sql = "DELETE FROM AsyncRestVO vo WHERE vo.state = :state and vo.createDate < (NOW() - INTERVAL :period SECOND)";
                String sql = "DELETE FROM AsyncRestVO vo WHERE vo.state = :state and vo.createDate < :period";
                EntityManager mgr = getEntityManager();
                EntityTransaction tran = mgr.getTransaction();
                try {
                    tran.begin();
                    Query query = mgr.createQuery(sql);
                    query.setParameter("state", AsyncRestState.done);
                    int completedApiExpiredPeriod =  RestGlobalConfig.COMPLETED_API_EXPIRED_PERIOD.value(Integer.class);
                    Timestamp period = Timestamp.valueOf(LocalDateTime.now()
                            .minusSeconds(completedApiExpiredPeriod));
                    query.setParameter("period", period);
                    query.executeUpdate();
                    tran.commit();
                } catch (Exception ex) {
                    tran.rollback();
                    throw new CloudRuntimeException(ex);
                } finally {
                    mgr.close();
                }
            }
        });
    }

    @Override
    public boolean stop() {
        return true;
    }
}
