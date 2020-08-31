package es.ubu.lsi.ubumonitor.webservice.api.tool.mobile;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunction;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ToolMobileCallExternalFunctions extends WSFunctionAbstract {

	private JSONArray requests;

	public ToolMobileCallExternalFunctions() {
		super(WSFunctionEnum.TOOL_MOBILE_CALL_EXTERNAL_FUNCTIONS);
		requests = new JSONArray();
		parameters.put("requests", requests);
	}

	public void addExternalFunction(WSFunction wsFunction) {
		addExternalFunction(wsFunction.getWSFunction()
				.toString(),
				wsFunction.getParameters()
						.toString(),
				null, null, null, null);

	}

	public void addExternalFunction(String function, String arguments, Boolean settingraw, Boolean settingfilter,
			Boolean settingfileurl, String settinglang) {
		JSONObject request = new JSONObject();
		request.put("function", function);
		request.put("arguments", arguments);

		putOpt(request, "settingraw", settingraw);
		putOpt(request, "settingfileurl", settingfileurl);
		putOpt(request, "settingfilter", settingfilter);
		request.putOpt("settinglang", settinglang);
		requests.put(request);

	}

	private void putOpt(JSONObject jsonObject, String key, Boolean bool) {
		if (bool != null) {
			jsonObject.put(key, bool.booleanValue() ? 1 : 0);
		}
	}
	

}
