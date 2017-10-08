package com.syscxp.core.config;

import com.syscxp.header.message.LocalEvent;

public class GlobalConfigUpdateEvent extends LocalEvent {
	private GlobalConfig oldConfig;
	private GlobalConfig newConfig;
	
	@Override
	public String getSubCategory() {
		return "GlobalConfig";
	}

    public GlobalConfig getOldConfig() {
        return oldConfig;
    }

    public void setOldConfig(GlobalConfig oldConfig) {
        this.oldConfig = oldConfig;
    }

    public GlobalConfig getNewConfig() {
        return newConfig;
    }

    public void setNewConfig(GlobalConfig newConfig) {
        this.newConfig = newConfig;
    }
}
