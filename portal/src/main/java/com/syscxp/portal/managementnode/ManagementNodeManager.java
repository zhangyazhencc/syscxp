package com.syscxp.portal.managementnode;

import com.syscxp.header.Service;

public interface ManagementNodeManager extends Service {
	void startNode();

	void quit(String reason);
}
