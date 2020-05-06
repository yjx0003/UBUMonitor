package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.util.Map;

public interface WSFunction {
	public WSFunctionEnum getWSFunction();
	public void addToMapParemeters();
	public Map<String, String> getParameters();
	public void clearParameters();
}
