package com.syscxp.billing.header.renew;

import com.syscxp.billing.header.sla.ProductCaller;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.retry.Retry;
import com.syscxp.core.retry.RetryCondition;
import com.syscxp.header.billing.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.rest.TimeoutRestTemplate;
import com.syscxp.header.tunnel.tunnel.APIRenewAutoTunnelMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateInterfaceExpireDateMsg;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ListIterator;

@Component
public class RenewJob{

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade restf;

    private TimeoutRestTemplate template;

    private static final CLogger logger = Utils.getLogger(RenewJob.class);
    public RenewJob() {
        template = RESTFacade.createRestTemplate(CoreGlobalProperty.REST_FACADE_READ_TIMEOUT, CoreGlobalProperty.REST_FACADE_CONNECT_TIMEOUT);
    }
    @Scheduled(cron = "0 0/5 * * * ? ")
    @Transactional
    protected void autoRenew() {

        GLock lock = new GLock(String.format("id-%s", "createRenew"), 120);
        lock.lock();
        try {

            SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
            q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
            List<RenewVO> renewVOs = q.list();
            if(renewVOs == null){
                logger.info("there is no activity renew product");
                return;
            }
            logger.info("the demon thread was going to autoRenew");
            ListIterator<RenewVO> ite = renewVOs.listIterator();
            while (ite.hasNext()) {
                RenewVO renewVO = ite.next();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiredTime = renewVO.getExpiredTime().toLocalDateTime();
                if ( ChronoUnit.DAYS.between(now, expiredTime) > 7 && now.isAfter(expiredTime)) {
                    dbf.getEntityManager().remove(dbf.getEntityManager().merge(renewVO));
                    dbf.getEntityManager().flush();
                    continue;
                }
                if(expiredTime.isAfter(now)){
                    continue;
                }

                ProductCaller caller = new ProductCaller(renewVO.getProductType());

                if(renewVO.getProductType().equals(ProductType.TUNNEL)){
                    APIRenewAutoTunnelMsg aMsg = new APIRenewAutoTunnelMsg();

                    aMsg.setUuid(renewVO.getProductUuid());
                    aMsg.setDuration(1);
                    aMsg.setProductChargeModel(renewVO.getProductChargeModel());
                    aMsg.setAccountUuid(renewVO.getAccountUuid());
                    InnerMessageHelper.setMD5(aMsg);
                    String gstr = RESTApiDecoder.dumpWithSession(aMsg);
                    RestAPIResponse rsp = syncJsonPost(caller.getProductUrl(), gstr,RestAPIResponse.class);

                    if (rsp.getState().equals(RestAPIState.Done.toString())) {
                        try {
                            RESTApiDecoder.loads(rsp.getResult());
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }

            }

        } finally {
            lock.unlock();
        }

    }



    public <T> T syncJsonPost(String url, String body,  Class<T> returnClass) {
        body = body == null ? "" : body;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentLength(body.length());
        HttpEntity<String> req = new HttpEntity<String>(body, requestHeaders);

        ResponseEntity<String> rsp = new Retry<ResponseEntity<String>>() {
            @Override
            @RetryCondition(onExceptions = {IOException.class, RestClientException.class})
            protected ResponseEntity<String> call() {
                return template.exchange(url, HttpMethod.POST, req, String.class);
            }
        }.run();

        if (rsp.getStatusCode() != org.springframework.http.HttpStatus.OK) {
            throw new OperationFailureException(Platform.operr("failed to post to %s, status code: %s, response body: %s", url, rsp.getStatusCode(), rsp.getBody()));
        }

        if (rsp.getBody() != null && returnClass != Void.class) {

            return JSONObjectUtil.toObject(rsp.getBody(), returnClass);
        } else {
            return null;
        }
    }


}
