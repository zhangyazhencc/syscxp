package org.zstack.vpn.manage;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.header.agent.AgentCommand;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

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
        this(VpnGlobalProperty.VPN_CONTROLLER_URL);
    }

    private RestAPIResponse checkState(String path, AgentCommand cmd) {
        String url =  URLBuilder.buildUrlFromBase(baseUrl, path);
        String res = restf.getRESTTemplate().getForObject(url, String.class);
        return JSONObjectUtil.toObject(res, RestAPIResponse.class);
    }

    public RestAPIResponse syncPost(String path, AgentCommand cmd, long interval, long timeout) throws InterruptedException {
        String cmdStr = JSONObjectUtil.toJsonString(cmd);
        String url = URLBuilder.buildUrlFromBase(baseUrl, path);
        RestAPIResponse rsp = restf.syncJsonPost(url, cmdStr, RestAPIResponse.class);
        long curr = 0;
        while (!rsp.getState().equals(RestAPIState.Done.toString()) && curr < timeout) {
            Thread.sleep(interval);
            rsp = checkState(path,cmd);
            curr += interval;
        }

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, result uuid:%s", curr, rsp.getUuid()));
        }

        return rsp;
    }


    public RestAPIResponse syncPost(String url, AgentCommand cmd) throws InterruptedException {
        return syncPost(url, cmd, 0, TimeUnit.SECONDS.toMillis(15));
    }

}

