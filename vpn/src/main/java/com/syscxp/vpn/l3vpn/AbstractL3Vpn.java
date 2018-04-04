package com.syscxp.vpn.l3vpn;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.l3vpn.L3VpnCommands.AgentCommand;
import com.syscxp.vpn.l3vpn.L3VpnCommands.AgentResponse;
import com.syscxp.vpn.vpn.VpnGlobalProperty;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author wangjie
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public abstract class AbstractL3Vpn {
    private static final CLogger LOGGER = Utils.getLogger(AbstractL3Vpn.class);

    @Autowired
    protected RESTFacade restf;
    @Autowired
    protected ErrorFacade errf;

    protected VpnVO self;
    protected final String id;
    private String baseUrl;
    private String scheme = VpnGlobalProperty.AGENT_URL_SCHEME;
    private int port = VpnGlobalProperty.AGENT_PORT;
    private String rootPath = VpnGlobalProperty.AGENT_URL_ROOT_PATH;

    protected AbstractL3Vpn(VpnVO self) {
        this.self = self;
        id = "Vpn-" + self.getUuid();

        if ("".equals(rootPath)) {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port);
        } else {
            baseUrl = URLBuilder.buildUrl(scheme, self.getVpnHost().getHostIp(), port, rootPath);
        }
    }

    public <T extends AgentResponse> void httpCall(final String path, final AgentCommand cmd, final Class<T> retClass, final ReturnValueCompletion<T> completion) {
        try {
            T rsp = restf.syncJsonPost(makeHttpPath(getBaseUrl(), path), cmd, retClass);
            completion.success(rsp);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
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
