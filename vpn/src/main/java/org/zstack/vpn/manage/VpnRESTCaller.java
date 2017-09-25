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
import org.zstack.vpn.manage.VpnCommands.*;

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
        this(VpnGlobalProperty.VPN_BASE_URL);
    }

    public AgentResponse checkState(AgentCommand cmd, long interval, long timeout) {
        String url = buildUrl(VpnConstant.CHECK_TASK_STATE_PATH);
        String cmdStr = JSONObjectUtil.toJsonString(cmd);
        long curr = 0;
        CheckStateResponse rsp;
        do {
            rsp = restf.getRESTTemplate().postForObject(url, cmdStr, CheckStateResponse.class);
        } while (!rsp.isSuccess() && curr < timeout);
        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error message", curr, rsp.getError()));
        }

        return rsp;
    }


    public CheckStateResponse checkState(String path, AgentCommand cmd) {
        String url = buildUrl(path);
        String body = JSONObjectUtil.toJsonString(cmd);

        ResponseEntity<String> rsp = new Retry<ResponseEntity<String>>() {
            @Override
            @RetryCondition(onExceptions = {IOException.class, RestClientException.class})
            protected ResponseEntity<String> call() {
                return restf.getRESTTemplate().postForEntity(url, body, String.class);
            }
        }.run();

        CheckStateResponse response = JSONObjectUtil.toObject(rsp.getBody(), CheckStateResponse.class);
        if (response == null) {
            response = new CheckStateResponse();
        }
        response.setStatusCode(rsp.getStatusCode());
        return response;
    }


    public AgentResponse syncPost(String path, AgentCommand cmd, long interval, long timeout) {
        String cmdStr = JSONObjectUtil.toJsonString(cmd);
        String url = buildUrl(path);
        AgentResponse rsp = restf.syncJsonPost(url, cmdStr, AgentResponse.class);
        long curr = 0;
        while (!rsp.isSuccess() && curr < timeout) {
            rsp = checkState(path, cmd);
            curr += interval;
        }

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error message", curr, rsp.getError()));
        }

        return rsp;
    }

    public AgentResponse syncPost(String url, AgentCommand cmd) {
        return syncPost(url, cmd, 500, TimeUnit.SECONDS.toMillis(15));
    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }
}

