package com.syscxp.core.puppet;

public interface PuppetConstant {
    String SERVICE_ID = "PuppetFacade";
    
    enum PuppetGlobalConfig {
        useJobQueue;
        
        public String getCategory() {
            return "Puppet";
        }
    }
}
