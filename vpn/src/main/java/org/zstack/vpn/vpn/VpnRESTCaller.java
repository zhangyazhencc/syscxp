package org.zstack.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.core.retry.Retry;
import org.zstack.core.retry.RetryCondition;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.rest.RESTConstant;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.vpn.VpnCommands.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnRESTCaller {
    private static final CLogger logger = Utils.getLogger(VpnRESTCaller.class);
    @Autowired
    private RESTFacade restf;

    private String baseUrl;

    public VpnRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public VpnRESTCaller() {
        this(VpnGlobalProperty.VPN_BASE_URL);
    }

    public CheckStatusResponse asyncCheckState(String path, AgentCommand cmd, long interval, long timeout) throws InterruptedException {
        long curr = 0;
        CheckStatusResponse rsp;
        do {
            TimeUnit.SECONDS.sleep(interval);
            rsp = checkState(path, cmd);
            curr += interval;
        }
        while ((rsp.getStatusCode() != HttpStatus.OK || rsp.getStatus() == ResponseStatus.Creating) && curr < timeout);

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error", curr, rsp.getError()));
        }
        return rsp;
    }

    public CheckStatusResponse asyncCheckState(String path, AgentCommand cmd) throws InterruptedException {
        return asyncCheckState(path, cmd, 1, TimeUnit.MINUTES.toSeconds(10));
    }


    public CheckStatusResponse checkState(String path, AgentCommand cmd) {
        return syncPostForVpn(path, cmd, CheckStatusResponse.class);
    }


    public <T extends AgentResponse> T syncPostForVpn(String path, AgentCommand cmd, Class<T> rspClass) {
        String body = JSONObjectUtil.toJsonString(cmd);
        String url = buildUrl(path);

        HttpHeaders requestHeaders = new HttpHeaders();
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
                return restf.getRESTTemplate().exchange(url, HttpMethod.POST, req, String.class);
            }
        }.run();
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("[http response(url: %s)] %s", url, rsp.getBody()));
        }

        T response = JSONObjectUtil.toObject(rsp.getBody(), rspClass);

        if (response == null) {
            response = (T) new AgentResponse();
        }
        response.setStatusCode(rsp.getStatusCode());

        return response;
    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }

    public RestAPIResponse syncPostForResult(APIMessage msg, long interval, long timeout) {
        return restf.syncJsonPostForResult(baseUrl, msg, interval, timeout);
    }

    public RestAPIResponse syncPostForResult(APIMessage msg) {
        return syncPostForResult(msg, 500, TimeUnit.SECONDS.toMillis(15));
    }
}

