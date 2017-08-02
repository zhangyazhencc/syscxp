package org.zstack.billing.header.identity;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

public class APIGetAccountBalanceMsg extends APISyncCallMessage{

	private static final long serialVersionUID = -1865535181343567377L;
	
	@APIParam(nonempty = true)
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	

}
