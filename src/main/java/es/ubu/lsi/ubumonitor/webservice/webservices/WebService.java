package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService {

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private String moodleWSRestFormat = "json";
	private String hostWithWS;
	private String hostWithAjax;
	private String sesskey;

	private String token;

	/**
	 * Intenta conectarse al servicio de moodle para recuperar el token del usuario
	 * para la API.
	 * 
	 * @param host     host de moodle
	 * @param userName username de moodle
	 * @param password contraseña de moodle
	 * @throws IOException si no se ha podido conectar al host
	 */
	public WebService(String host, String username, String password) throws IOException {

		String url = host + "/login/token.php";
		RequestBody formBody = new FormBody.Builder().add("username", username)
				.add("password", password)
				.add("service", WSFunctionEnum.MOODLE_MOBILE_APP.toString())
				.build();
		Response response = Connection.getResponse(new Request.Builder().url(url)
				.post(formBody)
				.build());

		JSONObject jsonObject = new JSONObject(new JSONTokener(response.body()
				.byteStream()));
		response.close();
		if (jsonObject.has("error")) {
			throw new IllegalAccessError(jsonObject.getString("error"));
		}

		setData(host, jsonObject.getString("token"));
	}

	public WebService(String host, String token) {
		setData(host, token);

	}

	private void setData(String host, String token) {
		this.token = token;

		this.hostWithWS = host + "/webservice/rest/server.php";
		this.hostWithAjax = host + "/lib/ajax/service.php";
	}

	public void setSesskey(String sesskey) {
		this.sesskey = sesskey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getmoodleWSRestFormat() {
		return moodleWSRestFormat;
	}

	public void setmoodleWSRestFormat(String moodleWSRestFormat) {
		this.moodleWSRestFormat = moodleWSRestFormat;
	}

	public Response getResponse(WSFunction wsFunction) throws IOException {

		wsFunction.clearParameters();
		wsFunction.addToMapParemeters();
		HttpUrl.Builder httpBuilder = HttpUrl.parse(hostWithWS)
				.newBuilder();

		httpBuilder.addQueryParameter("wsfunction", wsFunction.getWSFunction()
				.toString());
		httpBuilder.addQueryParameter("moodlewsrestformat", moodleWSRestFormat);
		httpBuilder.addQueryParameter("wstoken", token);

		for (Map.Entry<String, String> param : wsFunction.getParameters()
				.entrySet()) {
			httpBuilder.addQueryParameter(param.getKey(), param.getValue());

		}
		return Connection.getResponse(new Request.Builder().url(httpBuilder.build())
				.build());

	}

	public Response getAjaxResponse(WSFunction... wsFunctions) throws IOException {
		return WebService.getResponse(hostWithAjax + "?sesskey=" + sesskey, wsFunctions);
	}

	public static Response getAjaxResponse(String host, WSFunction... wsFunctions) throws IOException {
		String hosturl = host + "/lib/ajax/service.php";
		return getResponse(hosturl, wsFunctions);
	}

	public static Response getAjaxResponse(String host, String sesskey, WSFunction... wsFunctions) throws IOException {
		return getResponse(host + "/lib/ajax/service.php?sesskey=" + sesskey, wsFunctions);
	}

	private static Response getResponse(String host, WSFunction... wsFunctions) throws IOException {
		JSONArray jsonArray = new JSONArray();

		for (int i = 0; i < wsFunctions.length; ++i) {
			JSONObject methods = new JSONObject();
			methods.put("index", i);
			methods.put("methodname", wsFunctions[i].getWSFunction()
					.toString());
			methods.put("args", wsFunctions[i].getParameters());
			jsonArray.put(methods);
		}

		RequestBody body = RequestBody.create(jsonArray.toString(), JSON);
		return Connection.getResponse(new Request.Builder().url(host)
				.post(body)
				.build());
	}

}
