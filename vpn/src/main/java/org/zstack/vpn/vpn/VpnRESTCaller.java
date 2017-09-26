package org.zstack.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.zstack.core.rest.RESTApiDecoder;
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
            TimeUnit.MILLISECONDS.sleep(interval);
            rsp = queryState(path, cmd);
            curr += interval;
        }
        while (rsp.getStatusCode() != HttpStatus.OK && rsp.getState() == VpnCreateState.Creating && curr < timeout);

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error", curr, rsp.getError()));
        }
        return rsp;
    }

    public CheckStateResponse checkState(String path, AgentCommand cmd) throws InterruptedException {
        return checkState(path, cmd,1000, TimeUnit.MINUTES.toMillis(10));
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


    public <T extends AgentResponse> T syncPostForVPN(String path, AgentCommand cmd, Class<T>  rspClass) {
        String cmdStr = JSONObjectUtil.toJsonString(cmd);
        String url = buildUrl(path);
        return restf.syncJsonPost(url, cmdStr, rspClass);
    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }

    private RestAPIResponse queryResponse(String uuid) {
        String url =  URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_RESULT, uuid);
        String res = restf.getRESTTemplate().getForObject(url, String.class);
        return JSONObjectUtil.toObject(res, RestAPIResponse.class);
    }

    public RestAPIResponse syncPost(String path, APIMessage msg, long interval, long timeout) throws InterruptedException {
        String msgStr = RESTApiDecoder.dump(msg);
        String url = URLBuilder.buildUrlFromBase(baseUrl, path);
        RestAPIResponse rsp = restf.syncJsonPost(url, msgStr, RestAPIResponse.class);
        long curr = 0;
        while (!rsp.getState().equals(RestAPIState.Done.toString()) && curr < timeout) {
            TimeUnit.MILLISECONDS.sleep(interval);
            rsp = queryResponse(rsp.getUuid());
            curr += interval;
        }

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, result uuid:%s", curr, rsp.getUuid()));
        }

        return rsp;
    }

    public RestAPIResponse syncPost(String url, APIMessage msg) throws InterruptedException {
        return syncPost(url, msg, 500, TimeUnit.SECONDS.toMillis(15));
    }
}

