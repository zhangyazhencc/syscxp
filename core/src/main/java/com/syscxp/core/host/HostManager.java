package com.syscxp.core.host;

import com.syscxp.header.host.HostBaseExtensionFactory;
import com.syscxp.header.host.HostFactory;
import com.syscxp.header.host.HostType;
import com.syscxp.header.message.Message;

public interface HostManager {
    HostFactory getHostFactory(HostType type);
    
	void handleMessage(Message msg);

	HostBaseExtensionFactory getHostBaseExtensionFactory(Message msg);
}
