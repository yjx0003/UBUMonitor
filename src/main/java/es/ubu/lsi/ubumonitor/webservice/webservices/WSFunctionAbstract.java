package es.ubu.lsi.ubumonitor.webservice.webservices;

import org.json.JSONObject;

public abstract class WSFunctionAbstract implements WSFunction {
	
	private WSFunctionEnum webserviceFunctions;
	protected JSONObject parameters;
	
	public WSFunctionAbstract(WSFunctionEnum webserviceFunctions) {
		this.webserviceFunctions = webserviceFunctions;
		parameters = new JSONObject();
	}
	
	@Override
	public WSFunctionEnum getWSFunction() {
		return webserviceFunctions;
	}

	@Override
	public JSONObject getParameters() {
		return parameters;
	}	
	
	@Override
	public String toString() {
		return webserviceFunctions.toString();
	}
	
	@Override
	public void addToMapParemeters() {
		//default do nothing
	}
	
	
	
}
