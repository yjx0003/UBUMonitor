package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa las actividades o recursos presentes en un curso.
 * 
 * @author Yi Peng Ji
 *
 */
public class CourseModule implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * course module id
	 */
	private int cmid;

	/**
	 * activity url Optional.
	 * 
	 */
	private String url;

	/**
	 * activity module name
	 */
	private String moduleName;

	/**
	 * instance id
	 */
	private int instance;

	/**
	 * Optional. activity description
	 */
	private String description;

	/**
	 * Optional. is the module visible
	 */
	private boolean visible;

	/**
	 * Optional. Is the module visible for the user?
	 */
	private boolean uservisible;
	/**
	 * Optional. Availability information.
	 */
	private String availabilityinfo;

	/**
	 * Optional. is the module visible on course page
	 */
	private boolean visibleoncoursepage;
	/**
	 * activity icon url
	 */
	private String modicon;

	/**
	 * activity module type
	 */
	private ModuleType moduleType;

	/**
	 * activity module plural name
	 */
	private String modplural;

	/**
	 * Optional. module availability settings
	 */
	private String availability;

	/**
	 * number of identation in the site
	 */
	private int indent;

	private Section section;
	
	private Map<EnrolledUser, ActivityCompletion> activitiesCompletion;

	public CourseModule(int id) {
		setCmid(id);
		activitiesCompletion = new HashMap<>();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String name) {
		this.moduleName = name;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isUservisible() {
		return uservisible;
	}

	public void setUservisible(boolean uservisible) {
		this.uservisible = uservisible;
	}


	public String getAvailabilityinfo() {
		return availabilityinfo;
	}

	public void setAvailabilityinfo(String availabilityinfo) {
		this.availabilityinfo = availabilityinfo;
	}

	public boolean isVisibleoncoursepage() {
		return visibleoncoursepage;
	}

	public void setVisibleoncoursepage(boolean visibleoncoursepage) {
		this.visibleoncoursepage = visibleoncoursepage;
	}

	public void setVisibleoncoursepage(int visibleoncoursepage) {
		this.visibleoncoursepage = visibleoncoursepage != 0;

	}

	public String getModicon() {
		return modicon;
	}

	public void setModicon(String modicon) {
		this.modicon = modicon;
	}

	public ModuleType getModuleType() {
		return moduleType;
	}

	public void setModuleType(ModuleType modname) {
		this.moduleType = modname;
	}

	public String getModplural() {
		return modplural;
	}

	public void setModplural(String modplural) {
		this.modplural = modplural;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public int getCmid() {
		return cmid;
	}

	public void setCmid(int id) {
		this.cmid = id;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@Override
	public String toString() {
		return moduleName;
	}

	@Override
	public int hashCode() {
		return cmid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof CourseModule))
			return false;
		CourseModule other = (CourseModule) obj;
		return cmid == other.cmid;

	}

	public Map<EnrolledUser, ActivityCompletion> getActivitiesCompletion() {
		if (activitiesCompletion == null) {
			activitiesCompletion = new HashMap<>();
		}
		return activitiesCompletion;
	}

	public void setActivitiesCompletion(Map<EnrolledUser, ActivityCompletion> activitiesCompletion) {
		this.activitiesCompletion = activitiesCompletion;
	}

}
