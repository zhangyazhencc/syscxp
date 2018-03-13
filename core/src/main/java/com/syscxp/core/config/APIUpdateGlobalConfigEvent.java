package com.syscxp.core.config;

import com.syscxp.header.message.APIEvent;

public class APIUpdateGlobalConfigEvent extends APIEvent {
	private GlobalConfigInventory inventory;
	
	public APIUpdateGlobalConfigEvent(String apiId) {
	    super(apiId);
    }
	public APIUpdateGlobalConfigEvent() {
		super(null);
	}
	public GlobalConfigInventory getInventory() {
    	return inventory;
    }
	public void setInventory(GlobalConfigInventory inventory) {
    	this.inventory = inventory;
    }
 
    public static APIUpdateGlobalConfigEvent __example__() {
        APIUpdateGlobalConfigEvent event = new APIUpdateGlobalConfigEvent();
		GlobalConfigInventory inventory  = new GlobalConfigInventory();
        inventory.setCategory("quota");
        inventory.setName("scheduler.num");
        inventory.setValue("90");
        inventory.setDescription("default quota for scheduler.num");
        inventory.setDefaultValue("80");
        return event;
    }

}
