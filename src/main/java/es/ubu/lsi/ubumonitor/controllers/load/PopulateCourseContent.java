package es.ubu.lsi.ubumonitor.controllers.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetContents;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.util.Pair;

public class PopulateCourseContent {
	private WebService webService;
	private DataBase dataBase;


	public PopulateCourseContent(WebService webService, DataBase dataBase) {
		this.webService = webService;
		this.dataBase = dataBase;
		
	}

	public Pair<List<Section>, List<CourseModule>> populateCourseContent(int courseid) {

		try {
			CoreCourseGetContents coreCourseGetContents = new CoreCourseGetContents(courseid);
			coreCourseGetContents.appendExcludecontents(true);
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, coreCourseGetContents);
			return populateCourseContent(jsonArray);
		} catch (Exception e) {
			return new Pair<>(Collections.emptyList(), Collections.emptyList());
		}
	}

	public  Pair<List<Section>, List<CourseModule>> populateCourseContent(JSONArray jsonArray) {
		List<Section> sections = new ArrayList<>();
		List<CourseModule> courseModules = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Section section =populateSection(jsonObject);
			sections.add(section);
			JSONArray modules = jsonObject.getJSONArray(Constants.MODULES);
			for (int j = 0; j < modules.length(); ++j) {
				CourseModule courseModule = populateCourseModule(modules.getJSONObject(j));
				courseModule.setSection(section);
				courseModules.add(courseModule);
			}
		}
		return new Pair<>(sections, courseModules);
	}

	private Section populateSection(JSONObject jsonObject) {
		Section section = dataBase.getSections()
				.getById(jsonObject.getInt(Constants.ID));
		section.setName(jsonObject.optString(Constants.NAME));
		section.setVisible(jsonObject.optInt(Constants.VISIBLE) == 1);
		section.setSummary(jsonObject.optString(Constants.SUMMARY));
		section.setSummaryformat(DescriptionFormat.get(jsonObject.optInt(Constants.SUMMARYFORMAT)));
		section.setHiddenbynumsections(jsonObject.optInt(Constants.HIDDENBYNUMSECTIONS));
		section.setUservisible(jsonObject.optBoolean(Constants.USERVISIBLE));

		return section;

	}

	private CourseModule populateCourseModule(JSONObject jsonObject) {
		CourseModule module = dataBase.getModules()
				.getById(jsonObject.getInt(Constants.ID));
		
		module.setUrl(jsonObject.optString(Constants.URL));
		module.setModuleName(jsonObject.optString(Constants.NAME));
		module.setInstance(jsonObject.optInt(Constants.INSTANCE));
		module.setVisible(jsonObject.optInt(Constants.VISIBLE) == 1);
		module.setUservisible(jsonObject.optBoolean(Constants.USERVISIBLE));
		module.setVisibleoncoursepage(jsonObject.optInt(Constants.VISIBLEONCOURSEPAGE) == 1);
		module.setModicon(jsonObject.optString(Constants.MODICON));
		module.setModuleType(ModuleType.get(jsonObject.optString(Constants.MODNAME)));
		module.setModplural(jsonObject.optString(Constants.MODPLURAL));
		module.setIndent(jsonObject.optInt(Constants.INDENT));

		return module;
	}
}
