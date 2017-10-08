package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class APIQuerySpeedRecordsReply extends APIQueryReply {
    private List<SpeedRecordsVO> inventories;

    public List<SpeedRecordsVO> getInventories() {
        return inventories;
    }

    public void setInventories(List<SpeedRecordsVO> inventories) {
        this.inventories = inventories;
    }
}
