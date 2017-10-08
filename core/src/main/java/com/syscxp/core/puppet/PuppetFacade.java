package com.syscxp.core.puppet;

public interface PuppetFacade {
	void deployModule(String nodeFileName, String nodeExpression, String modulePath);
}
