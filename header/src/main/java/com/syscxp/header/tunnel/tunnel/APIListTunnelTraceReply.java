package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Create by DCY on 2017/11/28
 */
public class APIListTunnelTraceReply extends APIReply {
    private List<LSPTraceInventory> lspTraceInventories;

    private List<VsiCurrentTraceInventory> vsiCurrentTraceInventories;

    private List<VsiTePathInventory> vsiTePathInventories;

    public List<LSPTraceInventory> getLspTraceInventories() {
        return lspTraceInventories;
    }

    public void setLspTraceInventories(List<LSPTraceInventory> lspTraceInventories) {
        this.lspTraceInventories = lspTraceInventories;
    }

    public List<VsiCurrentTraceInventory> getVsiCurrentTraceInventories() {
        return vsiCurrentTraceInventories;
    }

    public void setVsiCurrentTraceInventories(List<VsiCurrentTraceInventory> vsiCurrentTraceInventories) {
        this.vsiCurrentTraceInventories = vsiCurrentTraceInventories;
    }

    public List<VsiTePathInventory> getVsiTePathInventories() {
        return vsiTePathInventories;
    }

    public void setVsiTePathInventories(List<VsiTePathInventory> vsiTePathInventories) {
        this.vsiTePathInventories = vsiTePathInventories;
    }
}
