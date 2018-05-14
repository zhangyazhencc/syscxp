package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/9
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateTEConfigMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = TunnelVO.class)
    private String tunnelUuid;
    @APIParam(emptyString = false,validValues = {"MAIN", "SPARE"})
    private TETraceType traceType;
    @APIParam(required = false)
    private String inNodes;
    @APIParam(required = false)
    private String exNodes;
    @APIParam(required = false)
    private String blurryInNodes;
    @APIParam(required = false)
    private String blurryExNodes;
    @APIParam(required = false)
    private String connInNodes;
    @APIParam(required = false)
    private String connExNodes;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public TETraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TETraceType traceType) {
        this.traceType = traceType;
    }

    public String getInNodes() {
        return inNodes;
    }

    public void setInNodes(String inNodes) {
        this.inNodes = inNodes;
    }

    public String getExNodes() {
        return exNodes;
    }

    public void setExNodes(String exNodes) {
        this.exNodes = exNodes;
    }

    public String getBlurryInNodes() {
        return blurryInNodes;
    }

    public void setBlurryInNodes(String blurryInNodes) {
        this.blurryInNodes = blurryInNodes;
    }

    public String getBlurryExNodes() {
        return blurryExNodes;
    }

    public void setBlurryExNodes(String blurryExNodes) {
        this.blurryExNodes = blurryExNodes;
    }

    public String getConnInNodes() {
        return connInNodes;
    }

    public void setConnInNodes(String connInNodes) {
        this.connInNodes = connInNodes;
    }

    public String getConnExNodes() {
        return connExNodes;
    }

    public void setConnExNodes(String connExNodes) {
        this.connExNodes = connExNodes;
    }
}
