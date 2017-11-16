package com.syscxp.billing.order;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.retry.Retry;
import com.syscxp.core.retry.RetryCondition;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.core.thread.TimerTask;
import com.syscxp.header.agent.OrderCallbackCmd;
import com.syscxp.header.billing.OrderInventory;
import com.syscxp.header.billing.OrderVO;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.TimeoutRestTemplate;
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
    private DatabaseFacade dbf;

    @Autowired
    private ThreadFacade threadFacade;
    private TimeoutRestTemplate template;

    public OrderNotifyJob() {
        template = RESTFacade.createRestTemplate(CoreGlobalProperty.REST_FACADE_READ_TIMEOUT, CoreGlobalProperty.REST_FACADE_CONNECT_TIMEOUT);
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void scheduleMethod() {
        SimpleQuery<NotifyOrderVO> qNotifyOrder = dbf.createQuery(NotifyOrderVO.class);
        qNotifyOrder.add(NotifyOrderVO_.status, SimpleQuery.Op.EQ, NotifyOrderStatus.FAILURE);
        List<NotifyOrderVO> notifyOrderVOs = qNotifyOrder.list();

        for (NotifyOrderVO notifyOrderVO : notifyOrderVOs) {
            NotifyOrderVO notifyOrderVO1 = dbf.findByUuid(notifyOrderVO.getUuid(), NotifyOrderVO.class);
            if (notifyOrderVO1.getStatus() != NotifyOrderStatus.FAILURE) continue;
            notifyOrderVO1.setStatus(NotifyOrderStatus.PROCESSING);
            dbf.updateAndRefresh(notifyOrderVO1);
            NotifyOrderVO notifyOrderVO2 = dbf.findByUuid(notifyOrderVO.getUuid(), NotifyOrderVO.class);
            String orderUuid = notifyOrderVO2.getOrderUuid();
            OrderVO orderVO = dbf.findByUuid(orderUuid, OrderVO.class);
            OrderCallbackCmd orderCallbackCmd = OrderCallbackCmd.valueOf(OrderInventory.valueOf(orderVO));
            threadFacade.submitTimerTask(new TimerTask() {
                @Override
                public boolean run() {
                    Map<String, String> header = new HashMap<>();
                    header.put(RESTConstant.COMMAND_PATH, "billing");
                    String body = JSONObjectUtil.toJsonString(orderCallbackCmd);
                    boolean flag = false;
                    try {
                        flag = syncJsonPost(notifyOrderVO2.getUrl(), body, header);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }finally {
                        int times = notifyOrderVO2.getNotifyTimes() + 1;
                        if (flag) {
                            notifyOrderVO2.setStatus(NotifyOrderStatus.SUCCESS);
                        } else {
                            if (times > 10) {
                                notifyOrderVO2.setStatus(NotifyOrderStatus.TERMINAL);
                            } else {
                                notifyOrderVO2.setStatus(NotifyOrderStatus.FAILURE);
                            }
                        }
                        notifyOrderVO2.setNotifyTimes(times);
                        dbf.updateAndRefresh(notifyOrderVO2);
                    }
                    return true;
                }
            }, TimeUnit.MINUTES, NotifyOrderInterval.getMinutes(notifyOrderVO.getNotifyTimes()+1));
        }

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
