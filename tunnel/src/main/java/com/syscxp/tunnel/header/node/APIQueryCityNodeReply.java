package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryCityNodeReply extends APIQueryReply {
    List<CityNodeInventory> cityNodeInventoryList;

    public List<CityNodeInventory> getCityNodeInventoryList() {
        return cityNodeInventoryList;
    }

    public void setCityNodeInventoryList(List<CityNodeInventory> cityNodeInventoryList) {
        this.cityNodeInventoryList = cityNodeInventoryList;
    }
}
