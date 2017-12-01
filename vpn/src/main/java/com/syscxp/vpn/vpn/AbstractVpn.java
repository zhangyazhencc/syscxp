package com.syscxp.vpn.vpn;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.exception.VpnErrors;
import com.syscxp.vpn.vpn.VpnCommands.AgentCommand;
import com.syscxp.vpn.vpn.VpnCommands.AgentResponse;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public abstract class AbstractVpn {
    private static final CLogger logger = Utils.getLogger(AbstractVpn.class);

    @Autowired
    protected RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    protected VpnVO self;
    protected final String id;
    private String baseUrl;
    private String scheme = VpnGlobalProperty.AGENT_URL_SCHEME;
    private int port = VpnGlobalProperty.AGENT_PORT;
    private String rootPath = VpnGlobalProperty.AGENT_URL_ROOT_PATH;

    protected AbstractVpn(VpnVO self) {
        this.self = self;
        id = "Vpn-" + self.getUuid();

        if (!"".equals(rootPath)) {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port, rootPath);
        } else {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port);
        }
    }

    public <T extends AgentResponse> void httpCall(final String path, final AgentCommand cmd, final Class<T> retClass, final ReturnValueCompletion<T> completion) {
        try {
            T rsp = restf.syncJsonPost(makeHttpPath(getBaseUrl(), path), cmd, retClass);
            if (rsp.isSuccess()){
                completion.success(rsp);
            } else {
                logger.debug(String.format("ERROR: %s", rsp.getError()));
                completion.fail(errf.instantiateErrorCode(VpnErrors.VPN_OPERATE_ERROR, rsp.getError()));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            completion.fail(errf.instantiateErrorCode(SysErrors.HTTP_ERROR, e.getMessage()));
        }
    }

    private String makeHttpPath(String baseUrl, String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, path);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public VpnVO getSelf() {
        return self;
    }
}
