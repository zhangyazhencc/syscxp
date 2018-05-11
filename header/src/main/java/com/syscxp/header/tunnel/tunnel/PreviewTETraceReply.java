package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.MessageReply;

import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
public class PreviewTETraceReply extends MessageReply {
    private String source;
    private String target;
    private Double length;
    private List<String> ipsaz;
    private List<String> ipsza;
    private List<String> nisaz;
    private List<String> nisza;

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

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public List<String> getIpsaz() {
        return ipsaz;
    }

    public void setIpsaz(List<String> ipsaz) {
        this.ipsaz = ipsaz;
    }

    public List<String> getIpsza() {
        return ipsza;
    }

    public void setIpsza(List<String> ipsza) {
        this.ipsza = ipsza;
    }

    public List<String> getNisaz() {
        return nisaz;
    }

    public void setNisaz(List<String> nisaz) {
        this.nisaz = nisaz;
    }

    public List<String> getNisza() {
        return nisza;
    }

    public void setNisza(List<String> nisza) {
        this.nisza = nisza;
    }
}
