package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

public class APIListAliRegionReply extends APIQueryReply {
    private List<AliRegionInventoey> aliRegionInventoeys;


    public List<AliRegionInventoey> getAliRegionInventoeys() {
        return aliRegionInventoeys;
    }

    public void setAliRegionInventoeys(List<AliRegionInventoey> aliRegionInventoeys) {
        this.aliRegionInventoeys = aliRegionInventoeys;
    }
}
