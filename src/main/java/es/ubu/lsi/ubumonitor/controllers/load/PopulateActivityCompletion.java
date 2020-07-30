package es.ubu.lsi.ubumonitor.controllers.load;

import java.time.Instant;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.State;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.Tracking;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.SubDataBase;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.completion.CoreCompletionGetActivitiesCompletionStatus;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

public class PopulateActivityCompletion {
	private static final Logger LOGGER = LoggerFactory.getLogger(PopulateActivityCompletion.class);

	private DataBase dataBase;
	private WebService webService;

	public PopulateActivityCompletion(DataBase dataBase, WebService webService) {
		this.dataBase = dataBase;
		this.webService = webService;
	}

	public void creeateActivitiesCompletionStatus(int courseid, Collection<EnrolledUser> users) {
		for (EnrolledUser user : users) {
			createActivitiesCompletionStatus(courseid, user.getId());
		}
	}

	public void createActivitiesCompletionStatus(int courseid, int userid) {

		try {
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService,
					new CoreCompletionGetActivitiesCompletionStatus(courseid, userid));
			createActivitiesCompletionStatus(jsonObject, dataBase.getUsers()
					.getById(userid));
		} catch (Exception e) {
			LOGGER.warn("Error in activity completion:", e);
		}

	}

	public void createActivitiesCompletionStatus(JSONObject jsonObject, EnrolledUser user) {
		SubDataBase<CourseModule> modules = dataBase.getModules();

		JSONArray statuses = jsonObject.getJSONArray(Constants.STATUSES);

		for (int i = 0; i < statuses.length(); i++) {
			JSONObject status = statuses.getJSONObject(i);

			CourseModule courseModule = modules.getById(status.getInt(Constants.CMID));
			State state = State.getByIndex(status.getInt(Constants.STATE));
			Instant timecompleted = Instant.ofEpochSecond(status.getLong(Constants.TIMECOMPLETED));
			Tracking tracking = Tracking.getByIndex(status.getInt(Constants.TRACKING));
			int iduser = status.optInt(Constants.OVERRIDEBY, -1);
			EnrolledUser overrideby = null;
			if (iduser != -1) {
				overrideby = dataBase.getUsers()
						.getById(iduser);
			}
			boolean valueused = status.optBoolean(Constants.VALUEUSED);
			ActivityCompletion activity = new ActivityCompletion(state, timecompleted, tracking, overrideby, valueused);

			courseModule.getActivitiesCompletion()
					.put(user, activity);

		}
	}
}
