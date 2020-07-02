package es.ubu.lsi.ubumonitor.webservice.api.tool.mobile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ToolMobileCallExternalFunctions extends WSFunctionAbstract {

	private List<WSFunctionAbstract> functions;

	public ToolMobileCallExternalFunctions() {
		super(WSFunctionEnum.TOOL_MOBILE_CALL_EXTERNAL_FUNCTIONS);
		functions = new ArrayList<>();
	}

	public void addFunction(WSFunctionAbstract wsFunctionAbstract) {
		functions.add(wsFunctionAbstract);
	}

	/**
	 * At the moment only works without array or object arguments.
	 */
	@Override
	public void addToMapParemeters() {
		for (int i = 0; i < functions.size(); ++i) {
			WSFunctionAbstract wsFunctionAbstract = functions.get(i);
			wsFunctionAbstract.addToMapParemeters();
			parameters.put("requests[" + i + "][function]", wsFunctionAbstract.toString());
			if (!wsFunctionAbstract.getParameters()
					.isEmpty()) {
				parameters.put("requests[" + i + "][arguments]",
						new JSONObject(wsFunctionAbstract.getParameters()).toString());
			}
		}

	}
}
