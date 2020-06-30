package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.util.HashMap;
import java.util.Map;

public abstract class WSFunctionAbstract implements WSFunction {
	
	private WSFunctionEnum webserviceFunctions;
	protected Map<String, String> parameters;
	
	public WSFunctionAbstract(WSFunctionEnum webserviceFunctions) {
		this.webserviceFunctions = webserviceFunctions;
		parameters = new HashMap<>();
	}
	
	@Override
	public WSFunctionEnum getWSFunction() {
		return webserviceFunctions;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}	
	
	@Override
	public void clearParameters() {
		parameters.clear();
	}
	
	@Override
	public String toString() {
		return webserviceFunctions.toString();
	}
	
	
}
