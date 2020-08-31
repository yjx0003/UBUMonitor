package es.ubu.lsi.ubumonitor.webservice.api.core.message;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreMessageSendInstantMessages extends WSFunctionAbstract {

	private JSONArray messages;

	public CoreMessageSendInstantMessages() {
		super(WSFunctionEnum.CORE_MESSAGE_SEND_INSTANT_MESSAGES);
		messages = new JSONArray();
		parameters.append("messages", messages);
	}

	public void append(int touserid, String text, DescriptionFormat textformat, String clientmsgid) {
		JSONObject message = new JSONObject();
		message.put("touserid", touserid);
		message.put("text", text);
		if (textformat != null) {
			message.put("textformat", textformat.getNumber());
		}
		if (clientmsgid != null) {
			message.put("clientmsgid", clientmsgid);
		}
		messages.put(message);
	}

	public void append(int touserid, String text) {
		append(touserid, text, null, null);
	}

	public void append(int touserid, String text, DescriptionFormat textFormat) {
		append(touserid, text, textFormat, null);
	}
}
