package com.syscxp.core.progressbar;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;

public interface ProgressBarFacade {
	void report(Message msg, String description);
	
	void report(Message msg, String description, long total, long current);
	
	void progagateContext(Message src, Message dest);
	
	void setContextToApiMessage(APIMessage msg);
}
