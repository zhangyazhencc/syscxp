package org.zstack.vpn.manage;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.zstack.core.retry.Retry;
import org.zstack.core.retry.RetryCondition;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.header.vpn.VpnState;
import org.zstack.vpn.manage.VpnCommands.*;

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

    public CheckStateResponse checkState(String path, AgentCommand cmd, long interval, long timeout) throws InterruptedException {
        long curr = 0;
        CheckStateResponse rsp;
        do {
            Thread.sleep(interval);
            rsp = queryState(path, cmd);
            curr += interval;
        }
        while (rsp.getStatusCode() != HttpStatus.OK && Objects.equals(rsp.getState(), VpnCreateState.Creating) && curr < timeout);

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error", curr, rsp.getError()));
        }
        return rsp;
    }

    public CheckStateResponse checkState(String path, AgentCommand cmd, long timeout) throws InterruptedException {
        return checkState(path, cmd, 1000, TimeUnit.MINUTES.toMillis(timeout));
    }

    public CheckStateResponse checkState(String path, AgentCommand cmd) throws InterruptedException {
        return checkState(path, cmd, 1000, TimeUnit.MINUTES.toMillis(10));
    }


    public CheckStateResponse queryState(String path, AgentCommand cmd) {
        String url = buildUrl(path);
        String body = JSONObjectUtil.toJsonString(cmd);

        ResponseEntity<String> rsp = restf.getRESTTemplate().postForEntity(url, body, String.class);

        CheckStateResponse response = JSONObjectUtil.toObject(rsp.getBody(), CheckStateResponse.class);
        if (response == null) {
            response = new CheckStateResponse();
        }
        response.setStatusCode(rsp.getStatusCode());
        return response;
    }


    public AgentResponse syncPost(String path, AgentCommand cmd) {
        String cmdStr = JSONObjectUtil.toJsonString(cmd);
        String url = buildUrl(path);
        return restf.syncJsonPost(url, cmdStr, AgentResponse.class);
    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }
}

