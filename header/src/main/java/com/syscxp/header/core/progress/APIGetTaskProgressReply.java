package com.syscxp.header.core.progress;

import com.syscxp.header.message.APIReply;

import java.util.List;

/**
 * Created by xing5 on 2017/3/21.
 */
public class APIGetTaskProgressReply extends APIReply {
    private List<TaskProgressInventory> inventories;

    public List<TaskProgressInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TaskProgressInventory> inventories) {
        this.inventories = inventories;
    }

    public static APIGetTaskProgressReply __example__() {
        APIGetTaskProgressReply msg = new APIGetTaskProgressReply();
        return msg;
    }
    
}