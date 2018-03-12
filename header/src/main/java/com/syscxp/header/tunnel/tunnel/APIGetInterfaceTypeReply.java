package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@RestResponse(fieldsTo = {"inventories"})
public class APIGetInterfaceTypeReply extends APIReply {
    private List<PortOfferingInventory> inventories;

    public List<PortOfferingInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<PortOfferingInventory> inventories) {
        this.inventories = inventories;
    }
}
