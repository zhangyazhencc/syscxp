package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.NeedReplyMessage;

import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
public class PreviewTETraceMsg extends NeedReplyMessage {
    private String source;
    private String target;
    private List<String> inNodes;
    private List<String> exNodes;
    private List<String> blurryInNodes;
    private List<String> blurryExNodes;
    private List<String> optimizationEdges;

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

    public List<String> getInNodes() {
        return inNodes;
    }

    public void setInNodes(List<String> inNodes) {
        this.inNodes = inNodes;
    }

    public List<String> getExNodes() {
        return exNodes;
    }

    public void setExNodes(List<String> exNodes) {
        this.exNodes = exNodes;
    }

    public List<String> getBlurryInNodes() {
        return blurryInNodes;
    }

    public void setBlurryInNodes(List<String> blurryInNodes) {
        this.blurryInNodes = blurryInNodes;
    }

    public List<String> getBlurryExNodes() {
        return blurryExNodes;
    }

    public void setBlurryExNodes(List<String> blurryExNodes) {
        this.blurryExNodes = blurryExNodes;
    }

    public List<String> getOptimizationEdges() {
        return optimizationEdges;
    }

    public void setOptimizationEdges(List<String> optimizationEdges) {
        this.optimizationEdges = optimizationEdges;
    }
}
