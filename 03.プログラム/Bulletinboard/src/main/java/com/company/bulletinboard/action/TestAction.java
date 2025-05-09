package com.company.bulletinboard.action;

import com.company.bulletinboard.interceptor.BaseAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(TestAction.class);

	@Override
	public String mainProc() throws Exception {
		logger.debug("TestAction execute method called");
		return SUCCESS;
	}

}
