package org.zstack.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.retry.Retry;
import org.zstack.core.retry.RetryCondition;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;
import org.zstack.header.vpn.VpnAgentCommand;
import org.zstack.header.vpn.VpnAgentResponse;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.vpn.VpnCommands.*;

import java.io.IOException;
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
        this(CoreGlobalProperty.VPN_BASE_URL);
    }

    public CheckStatusResponse asyncCheckState(String path, VpnAgentCommand cmd, long interval, long timeout) {
        long curr = 0;
        CheckStatusResponse rsp;
        do {
            try {
                TimeUnit.SECONDS.sleep(interval);
            } catch (InterruptedException e) {
                logger.debug(String.format("fail to get result[uuid: %s] from Url[%s]", cmd.getVpnUuid(), path));
            }
            rsp = checkState(path, cmd);
            curr += interval;
        }
        while ((rsp.getStatusCode() != HttpStatus.OK || rsp.getState() == RestAPIState.Processing) && curr < timeout);

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error", curr, rsp.getError()));
        }
        return rsp;
    }

    public CheckStatusResponse asyncCheckState(String path, VpnAgentCommand cmd) throws InterruptedException {
        return asyncCheckState(path, cmd, 1, TimeUnit.MINUTES.toSeconds(10));
    }


    public CheckStatusResponse checkState(String path, VpnAgentCommand cmd) {
        return syncPostForVpn(path, cmd, CheckStatusResponse.class);
    }


    public <T extends VpnAgentResponse> T syncPostForVpn(String path, VpnAgentCommand cmd, Class<T> rspClass) {
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
            response = (T) new VpnAgentResponse();
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

