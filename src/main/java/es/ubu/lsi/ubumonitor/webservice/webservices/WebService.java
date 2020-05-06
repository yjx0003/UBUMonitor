package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService {

	private static String moodleWSRestFormat = "json";
	private static String host;
	private static String token;

	private WebService() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Intenta conectarse al servicio de moodle para recuperar el token del usuario
	 * para la API.
	 * 
	 * @param host     host de moodle
	 * @param userName username de moodle
	 * @param password contraseña de moodle
	 * @throws IOException si no se ha podido conectar al host
	 */
	public static void initialize(String host, String username, String password) throws IOException {
		WebService.host = host + "/webservice/rest/server.php";
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
		String token = jsonObject.getString("token");

		initialize(token);
	}

	public static void initialize(String token) {
		WebService.token = token;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		WebService.host = host;
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		WebService.token = token;
	}

	public static String getmoodleWSRestFormat() {
		return moodleWSRestFormat;
	}

	public static void setmoodleWSRestFormat(String moodleWSRestFormat) {
		WebService.moodleWSRestFormat = moodleWSRestFormat;
	}

	public static Response getResponse(WSFunction wsFunction) throws IOException {
		wsFunction.clearParameters();
		wsFunction.addToMapParemeters();
		HttpUrl.Builder httpBuilder = HttpUrl.parse(host)
				.newBuilder();

		httpBuilder.addQueryParameter("wsfunction", wsFunction.getWSFunction()
				.toString());
		httpBuilder.addQueryParameter("moodlewsrestformat", moodleWSRestFormat);
		httpBuilder.addQueryParameter("wstoken", token);

		for (Map.Entry<String, String> param : wsFunction.getParameters()
				.entrySet()) {
			httpBuilder.addQueryParameter(param.getKey(), param.getValue());

		}
		System.out.println(httpBuilder.build().url());
		return Connection.getResponse(new Request.Builder().url(httpBuilder.build())
				.build());

	}
}
