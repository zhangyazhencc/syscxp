package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

public class APIListAliRegionReply extends APIQueryReply {
    private List<Map<String,String>> regions;

    public List<Map<String, String>> getRegions() {
        return regions;
    }

    public void setRegions(List<Map<String, String>> regions) {
        this.regions = regions;
    }
}
