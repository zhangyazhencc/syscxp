package com.syscxp.billing.order;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.retry.Retry;
import com.syscxp.core.retry.RetryCondition;
import com.syscxp.core.thread.CancelablePeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.thread.TimerTask;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.billing.OrderVO;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.TimeoutRestTemplate;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class OrderNotifyJob {
    private static final CLogger logger = Utils.getLogger(OrderNotifyJob.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ThreadFacade threadFacade;
    private TimeoutRestTemplate template;

    public OrderNotifyJob() {
        template = RESTFacade.createRestTemplate(CoreGlobalProperty.REST_FACADE_READ_TIMEOUT, CoreGlobalProperty.REST_FACADE_CONNECT_TIMEOUT);
    }

    @Scheduled(cron="0 0/1 * * * ? ")
    public void scheduleMethod(){
        SimpleQuery<NotifyOrderVO> qNotifyOrder = dbf.createQuery(NotifyOrderVO.class);
        qNotifyOrder.add(NotifyOrderVO_.status, SimpleQuery.Op.NOT_EQ, NotifyOrderStatus.SUCCESS);
        List<NotifyOrderVO> notifyOrderVOs = qNotifyOrder.list();

        for(NotifyOrderVO notifyOrderVO: notifyOrderVOs){
            NotifyOrderVO notifyOrderVO1 = dbf.findByUuid(notifyOrderVO.getUuid(),NotifyOrderVO.class);
            if(notifyOrderVO1.getStatus()!= NotifyOrderStatus.FAILURE) continue;
            notifyOrderVO1.setStatus(NotifyOrderStatus.PROCESSING);
            if(dbf.updateAndRefresh(notifyOrderVO1)==null)  continue;
            String orderUuid = notifyOrderVO1.getOrderUuid();
            OrderVO orderVO = dbf.findByUuid(orderUuid, OrderVO.class);
            OrderCallbackCmd orderCallbackCmd = OrderCallbackCmd.valueOf(orderVO);
            threadFacade.submitTimerTask(new TimerTask() {
                @Override
                public boolean run() {
                    Map<String, String> header = new HashMap<>();
                    header.put(RESTConstant.COMMAND_PATH, orderVO.getProductType().toString());
                    String body = JSONObjectUtil.toJsonString(orderCallbackCmd);
                    try {
                        boolean flag = syncJsonPost(notifyOrderVO1.getUrl(), body, header);
                        int times = notifyOrderVO1.getNotifyTimes()+1;
                        if(flag){
                            notifyOrderVO1.setStatus(NotifyOrderStatus.SUCCESS);
                        } else{
                            if(times >10){
                                notifyOrderVO1.setStatus(NotifyOrderStatus.TERMINAL);
                            }else{
                                notifyOrderVO1.setStatus(NotifyOrderStatus.FAILURE);
                            }
                        }
                        notifyOrderVO1.setNotifyTimes(times);
                        dbf.updateAndRefresh(notifyOrderVO1);
                    }catch (Exception e){
                        logger.error(e.getMessage());
                        int times = notifyOrderVO1.getNotifyTimes()+1;
                        notifyOrderVO1.setNotifyTimes(times);
                        if(times >10){
                            notifyOrderVO1.setStatus(NotifyOrderStatus.TERMINAL);
                        }else{
                            notifyOrderVO1.setStatus(NotifyOrderStatus.FAILURE);
                        }
                        dbf.updateAndRefresh(notifyOrderVO1);
                    }
                    return true;
                }
            },TimeUnit.MINUTES, NotifyOrderInterval.getMinutes(notifyOrderVO.getNotifyTimes()));
        }

    }


    public void echo(final String url, String command, OrderCallbackCmd orderCallbackCmd, final long interval, final long timeout) {
        class Notify implements CancelablePeriodicTask {
            private long count;

            Notify() {
                this.count = timeout / interval;
                DebugUtils.Assert(count != 0, String.format("invalid timeout[%s], interval[%s]", timeout, interval));
            }

            @Override
            public boolean run() {
                try {
                    Map<String, String> header = new HashMap<>();
                    header.put(RESTConstant.COMMAND_PATH, command);
                    String body = JSONObjectUtil.toJsonString(orderCallbackCmd);
                    return syncJsonPost(url, body, header);
                } catch (Exception e) {
                    if (--count <= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.MINUTES;
            }

            @Override
            public long getInterval() {
                return interval;
            }

            @Override
            public String getName() {
                return "Notify order";
            }
        }

        threadFacade.submitCancelablePeriodicTask(new Notify());
    }


    public boolean syncJsonPost(String url, String body, Map<String, String> headers) {
        body = body == null ? "" : body;

        HttpHeaders requestHeaders = new HttpHeaders();
        if (headers != null) {
            requestHeaders.setAll(headers);
        }
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(body.length());
        HttpEntity<String> req = new HttpEntity<String>(body, requestHeaders);
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("json post[%s], %s", url, req.toString()));
        }


        ResponseEntity<String> rsp = new Retry<ResponseEntity<String>>() {
            @Override
            @RetryCondition(onExceptions = {IOException.class, RestClientException.class})
            protected ResponseEntity<String> call() {
                return template.exchange(url, HttpMethod.POST, req, String.class);
            }
        }.run();

        if (rsp.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            return false;
        }

        return true;
    }
}
