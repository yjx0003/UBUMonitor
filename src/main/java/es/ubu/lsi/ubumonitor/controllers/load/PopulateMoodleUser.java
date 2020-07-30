package es.ubu.lsi.ubumonitor.controllers.load;

import java.time.Instant;
import java.time.ZoneId;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.users.CoreUserGetUsersByField;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

public class PopulateMoodleUser {

	private WebService webService;

	public PopulateMoodleUser(WebService webService) {
		this.webService = webService;
	}

	public MoodleUser populateMoodleUser(String username, String host) {

		try {
			CoreUserGetUsersByField coreUserGetUsersByField = new CoreUserGetUsersByField();
			coreUserGetUsersByField.setUsername(username);
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, coreUserGetUsersByField);
			return populateMoodleUser(jsonArray, host);
		} catch (Exception e) {
			return null;
		}
		
	}

	public MoodleUser populateMoodleUser(JSONArray jsonArray, String host) {
		JSONObject coreUserGetUsersByField = jsonArray.getJSONObject(0);
		MoodleUser moodleUser = new MoodleUser();
		moodleUser.setId(coreUserGetUsersByField.getInt(Constants.ID));

		moodleUser.setUserName(coreUserGetUsersByField.optString(Constants.USERNAME));

		moodleUser.setFullName(coreUserGetUsersByField.optString(Constants.FULLNAME));

		moodleUser.setEmail(coreUserGetUsersByField.optString(Constants.EMAIL));

		moodleUser.setFirstAccess(Instant.ofEpochSecond(coreUserGetUsersByField.optLong(Constants.FIRSTACCESS)));

		moodleUser.setLastAccess(Instant.ofEpochSecond(coreUserGetUsersByField.optLong(Constants.LASTACCESS)));

		byte[] imageBytes = UtilMethods
				.downloadImage(coreUserGetUsersByField.optString(Constants.PROFILEURLSMALL, null));

		moodleUser.setUserPhoto(imageBytes);

		moodleUser.setLang(coreUserGetUsersByField.optString(Constants.LANG));

		moodleUser.setServerTimezone(UtilMethods.findServerTimezone(host));
		// 99 significa que el usuario esta usando la zona horaria del servidor
		ZoneId userTimeZone = "99".equals(coreUserGetUsersByField.optString(Constants.TIMEZONE))
				? moodleUser.getServerTimezone()
				: ZoneId.of(coreUserGetUsersByField.optString(Constants.TIMEZONE, "UTC"));

		moodleUser.setTimezone(userTimeZone);

		return moodleUser;
	}
}
