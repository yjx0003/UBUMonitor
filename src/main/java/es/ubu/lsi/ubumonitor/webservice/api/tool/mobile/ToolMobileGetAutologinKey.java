package es.ubu.lsi.ubumonitor.webservice.api.tool.mobile;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ToolMobileGetAutologinKey extends WSFunctionAbstract{

	public ToolMobileGetAutologinKey() {
		super(WSFunctionEnum.TOOL_MOBILE_GET_AUTOLOGIN_KEY);
	}

	@Override
	public void addToMapParemeters() {
		// no parameters
		
	}

}
