package com.syscxp.core.rest;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusEventListener;
import com.syscxp.header.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import com.syscxp.header.Component;
import com.syscxp.header.apimediator.ApiMediatorConstant;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.rest.RESTApiFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.rest.RestAPIVO;
import com.syscxp.header.search.APISearchMessage;
import com.syscxp.utils.ExceptionDSL;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.*;

public class RESTApiFacadeImpl implements RESTApiFacade, CloudBusEventListener, Component {
    private static final CLogger logger = Utils.getLogger(RESTApiFacadeImpl.class);

    private EntityManagerFactory entityManagerFactory;
    private Set<String> basePkgNames;
    private List<String> processingRequests = Collections.synchronizedList(new ArrayList<String>(100));

    @Autowired
    private CloudBus bus;

    void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Set<APIEvent> boundEvents = new HashSet<APIEvent>(100);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.resetFilters(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(APIEvent.class));
        for (String pkg : getBasePkgNames()) {
            for (BeanDefinition bd : scanner.findCandidateComponents(pkg)) {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
//                if (clazz == APIEvent.class) {
//                    continue;
//                }
                APIEvent evt = (APIEvent) clazz.newInstance();
                boundEvents.add(evt);
            }
        }

        for (APIEvent e : boundEvents) {
            bus.subscribeEvent(this, e);
        }
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private RestAPIVO persist(APIMessage msg) {
        RestAPIVO vo = new RestAPIVO();
        vo.setUuid(msg.getId());
        vo.setApiMessageName(msg.getMessageName());
        vo.setState(RestAPIState.Processing);
        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            mgr.persist(vo);
            mgr.flush();
            mgr.refresh(vo);
            tran.commit();
            return vo;
        } catch (Exception e) {
            ExceptionDSL.exceptionSafe(tran::rollback);
            throw new CloudRuntimeException(e);
        } finally {
            ExceptionDSL.exceptionSafe(mgr::close);
        }
    }

    @Override
    public RestAPIResponse send(APIMessage msg) {
        assert !(msg instanceof APIListMessage) && !(msg instanceof APISearchMessage) : "You must invoke call(APIMessage) for APIListMessage or APISearchMsg, the message you pass is "
                + msg.getMessageName();
        RestAPIResponse rsp = new RestAPIResponse();
        RestAPIVO vo = persist(msg);
        processingRequests.add(vo.getUuid());
        rsp.setCreatedDate(vo.getCreateDate());
        rsp.setState(vo.getState().toString());
        rsp.setUuid(vo.getUuid());
        msg.setServiceId(ApiMediatorConstant.SERVICE_ID);
        bus.send(msg);
        return rsp;
    }

    @Override
    public RestAPIResponse call(APIMessage msg) {
        RestAPIResponse rsp = new RestAPIResponse();
        rsp.setCreatedDate(new Date());
        msg.setServiceId(ApiMediatorConstant.SERVICE_ID);
        MessageReply reply = bus.call(msg);
        rsp.setFinishedDate(new Date());
        rsp.setState(RestAPIState.Done.toString());
        rsp.setResult(RESTApiDecoder.dump(reply));
        return rsp;
    }

    private RestAPIVO find(String uuid) {
        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            RestAPIVO vo = mgr.find(RestAPIVO.class, uuid);
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
    public RestAPIResponse getResult(String uuid) {
        RestAPIVO vo = find(uuid);
        if (vo == null) {
            return null;
        }
        RestAPIResponse rsp = new RestAPIResponse();
        rsp.setCreatedDate(vo.getCreateDate());
        rsp.setFinishedDate(vo.getLastOpDate());
        rsp.setResult(vo.getResult());
        rsp.setState(vo.getState().toString());
        rsp.setUuid(vo.getUuid());
        return rsp;
    }

    private synchronized EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private boolean update(APIEvent e) {
        String sql = "update RestAPIVO r set r.result = :result, r.state = :state where r.uuid = :uuid";
        EntityManager mgr = getEntityManager();
        EntityTransaction tran = mgr.getTransaction();
        try {
            tran.begin();
            Query query = mgr.createQuery(sql);
            query.setParameter("result", RESTApiDecoder.dump(e));
            query.setParameter("state", RestAPIState.Done);
            query.setParameter("uuid", e.getApiId());
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

    @Override
    public boolean handleEvent(Event e) {
        try {
            if (e instanceof APIEvent) {
                APIEvent ae = (APIEvent) e;
                if (processingRequests.contains(ae.getApiId())) {
                    boolean ret = update(ae);
                    processingRequests.remove(ae.getApiId());
                    if (!ret) {
                        logger.warn(String.format("Cannot find RestAPIVO[uuid:%s], something wrong happened", ae.getApiId()));
                    }
                }
            } else {
                bus.dealWithUnknownMessage(e);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }

        return false;
    }

    public Set<String> getBasePkgNames() {
        if (basePkgNames == null) {
            basePkgNames = new HashSet<>();
            basePkgNames.add("com.syscxp");
        }
        return basePkgNames;
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
