package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Get course contents
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetContents extends WSFunctionAbstract {

	/**
	 * Options, used since Moodle 2.9
	 */
	private JSONArray options;

	public CoreCourseGetContents(int courseid) {
		super(WSFunctionEnum.CORE_COURSE_GET_CONTENTS);
		setCourseid(courseid);
		options = new JSONArray();
	}

	/**
	 * Course id.
	 * 
	 * @param courseid Course id
	 */
	public void setCourseid(int courseid) {
		parameters.put("courseid", courseid);
	}

	private void append(String name, Object value) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", name);
		jsonObject.put("value", value);
		options.put(jsonObject);
	}

	/**
	 * Do not return modules, return only the sections structure.
	 * 
	 * @param excludemodules true if not return modules, false otherwise
	 */
	public void appendExcludemodules(boolean excludemodules) {
		append("excludemodules", excludemodules);

	}

	/**
	 * Do not return module contents (i.e: files inside a resource).
	 * 
	 * @param excludecontents true if not return module contents false otherwise
	 */
	public void appendExcludecontents(boolean excludecontents) {
		append("excludecontents", excludecontents);
	}

	/**
	 * Return stealth modules for students in a special section (with id -1).
	 * 
	 * @param includestealthmodule
	 */
	public void appendIncludestealthmodules(boolean includestealthmodule) {
		append("includestealthmodule", includestealthmodule);
	}

	/**
	 * Return only this section.
	 * 
	 * @param sectionid section id
	 */
	public void appendSectionid(int sectionid) {
		append("sectionid", sectionid);
	}

	/**
	 * Return only this section with number (order).
	 * 
	 * @param sectionnumber section number
	 */
	public void appendSectionnumber(int sectionnumber) {
		append("sectionnumber", sectionnumber);
	}

	/**
	 * Return only this module information (among the whole sections structure).
	 * 
	 * @param cmid course module id
	 */
	public void appendCmid(int cmid) {
		append("cmid", cmid);
	}

	/**
	 * Return only modules with this name "label, forum, etc...".
	 * @param modname module name
	 */
	public void appendModname(ModuleType modname) {
		if (modname != null) {
			append("modname", modname.getModName());
		}
	}
	/**
	 * Return only the module with this id (to be used with modname).
	 * @param modid course module id
	 */
	public void appendModid(int modid) {
		append("modid", modid);
	}
}
