package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

public class APIListAliRegionReply extends APIQueryReply {
    private List<List> regions;


    public List<List> getRegions() {
        return regions;
    }

    public void setRegions(List<List> regions) {
        this.regions = regions;
    }
}
