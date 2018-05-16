package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/9
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
public class APIPreviewTETraceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String source;
    @APIParam(emptyString = false)
    private String target;
    @APIParam(required = false)
    private String inNodes;
    @APIParam(required = false)
    private String exNodes;
    @APIParam(required = false)
    private String blurryInNodes;
    @APIParam(required = false)
    private String blurryExNodes;
    @APIParam(required = false)
    private String optimizationEdges;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    public String getOptimizationEdges() {
        return optimizationEdges;
    }

    public void setOptimizationEdges(String optimizationEdges) {
        this.optimizationEdges = optimizationEdges;
    }
}
