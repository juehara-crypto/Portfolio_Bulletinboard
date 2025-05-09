package com.company.bulletinboard.action;

import com.company.bulletinboard.interceptor.BaseAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(SimpleAction.class);

	@Override
	public String mainProc() throws Exception {
		logger.debug("SimpleAction mainProc started");
		logger.debug("SimpleAction mainProc finished");
		return SUCCESS;
	}
}
