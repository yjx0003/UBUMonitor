package webservice;

import java.io.IOException;
import java.util.Set;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webservice.core.CoreUserGetUsersByField.Field;

public abstract class WebService {

	static final Logger logger = LoggerFactory.getLogger(WebService.class);

	private static String token;
	private static String urlWithToken;
	private final static String REST_FORMAT = "json";
	private String parameters;

	public WebService() {

		checkToken();

		this.parameters = "";

	}

	public static void initialize(String host, String token) {

		WebService.token = token;

		urlWithToken = host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=" + REST_FORMAT
				+ "&wsfunction=";

	}

	public static void initialize(String host, String userName, String password) throws IOException {
		String url = host + "/login/token.php?username=" + userName + "&password=" + password + "&service="
				+ WSFunctions.MOODLE_MOBILE_APP;

		String JSON = Jsoup.connect(url).ignoreContentType(true).execute().body();

		JSONObject jsonObject = new JSONObject(JSON);

		String token = jsonObject.getString("token");

		initialize(host, token);
	}

	private static void checkToken() {
		if (token == null) {
			throw new IllegalStateException(
					"Token no configurado, llama al metodo setToken de la clase WebService para configurarlo.");
		}
	}

	public static String getToken() {
		checkToken();
		return token;
	}

	public String getResponse() throws IOException {

		parameters = "";

		appendToUrlParameters();

		String url = urlWithToken + getWSFunction() + parameters;

		logger.info("Usando la url de moodle web service: " + "&wsfunction=" + getWSFunction() + parameters);

		return getContentWithJsoup(url);

	}

	public abstract WSFunctions getWSFunction();

	public String geCompletetUrl() {
		return urlWithToken + getWSFunction() + parameters;
	}

	protected abstract void appendToUrlParameters();

	protected void appendToUrlUserid(int userid) {
		if (userid != 0)
			parameters += "&userid=" + userid;
	}

	protected void appendToUrlCourseid(int courseid) {
		if (courseid != 0)
			parameters += "&courseid=" + courseid;
	}

	protected void appendToUrlCoursesids(Set<Integer> coursesids) {
		if (coursesids != null) {
			for (Integer courseid : coursesids) {
				parameters += "&courseids[]=" + courseid;
			}
		}
	}

	protected void appendToUrlGruopid(int groupid) {
		if (groupid != 0)
			parameters += "&groupid=" + groupid;
	}

	protected void appendToUrlValues(Set<String> values) {
		if (values != null) {
			for (String value : values) {
				parameters += "&values[]=" + value;
			}
		}
	}

	protected void appendToUrlField(Field field) {
		if (field != null)
			parameters += "&field=" + field;
	}

	protected void appendToUrlOptions(int index, String name, String value) {
		parameters += "&options[" + index + "][name]=" + name + "&options[" + index + "][value]=" + value;
	}

	protected void appendToUrlOptions(int index, String name, int value) {
		parameters += "&options[" + index + "][name]=" + name + "&options[" + index + "][value]=" + value;
	}

	protected void appendToUrlOptions(int index, String name, boolean value) {
		parameters += "&options[" + index + "][name]=" + name + "&options[" + index + "][value]=" + value;
	}

	private static String getContentWithJsoup(String url) throws IOException {
		return Jsoup.connect(url).ignoreContentType(true).execute().body();
	}

}
