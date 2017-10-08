package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

/**
 * Created by DCY on 2017-08-21
 */
public class APIGetDistinctNodeCityReply extends APIQueryReply {
    private List<String> cities;

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }
}
