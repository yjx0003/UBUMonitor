package webservice;

import java.io.IOException;
import java.util.Set;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import webservice.core.CoreUserGetUsersByField.Field;

public abstract class WebService {

	protected static String token;
	protected static String urlWithToken;
	protected String url;

	public WebService(String moodleOption) {
		if (token == null) {
			throw new IllegalStateException(
					"Token no configurado, llama al metodo setHostAndToken de la clase WebService para configurarlo.");
		}

		url = urlWithToken;
		url += moodleOption;

	}

	public static void setToken(String host, String username, String password) throws IOException {

		String url = host + "/login/token.php?username=" + username + "&password=" + password + "&service="
				+ MoodleOptions.SERVICIO_WEB_MOODLE;

		String JSON = getContentWithJsoup(url);

		JSONObject jsonObject = new JSONObject(JSON);

		token = jsonObject.getString("token");

		urlWithToken = host + "/webservice/rest/server.php?moodlewsrestformat=json&wstoken=" + token;

	}

	public void appendToUrlUserid(int userid) {
		url += "&userid=" + userid;
	}

	public void appendToUrlCourseid(int courseid) {
		url += "&courseid=" + courseid;
	}

	public void appendToUrlGruopid(int groupid) {
		url += "&groupid=" + groupid;
	}

	public void appendToUrlValues(Set<String> values) {
		
		for (String value : values) {
			url += "&values[]=" + value;
		}
	}
	
	public void appendToUrlField(Field field) {
		url+="&field="+field;
	}
	
	public void appendToUrlCoursesids(Set<Integer> coursesids) {
		for (Integer courseid:coursesids) {
			url += "&courseids[]=" + courseid;
		}
	}
	
	public static String getContentWithJsoup(String url) throws IOException {
		return Jsoup.connect(url).ignoreContentType(true).execute().body();
	}

	public abstract String getResponse() throws IOException;
}
