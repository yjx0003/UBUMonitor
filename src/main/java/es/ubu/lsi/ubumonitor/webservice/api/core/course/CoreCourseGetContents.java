package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetContents extends WSFunctionAbstract {
	/**
	 * Do not return modules, return only the sections structure
	 */
	private Boolean excludemodules;

	/**
	 * Do not return module contents (i.e: files inside a resource)
	 */
	private Boolean excludecontents;

	/**
	 * Return stealth modules for students in a special section (with id -1)
	 */
	private Boolean includestealthmodules;

	/**
	 * Return only this section
	 */
	private Integer sectionid;

	/**
	 * Return only this section with number (order)
	 */
	private Integer sectionnumber;

	/**
	 * Return only this module information (among the whole sections structure)
	 */
	private Integer cmid;

	/**
	 * Return only modules with this name "label, forum, etc..."
	 */
	private String modname;

	/**
	 * Return only the module with this id (to be used with modname)
	 */
	private Integer modid;

	private Integer courseid;

	public CoreCourseGetContents(Integer courseid) {
		super(WSFunctionEnum.CORE_COURSE_GET_CONTENTS);
		this.courseid = courseid;
	}

	private CoreCourseGetContents(Builder builder) {
		this(builder.courseid);
		excludemodules = builder.excludemodules;
		excludecontents = builder.excludecontents;
		includestealthmodules = builder.includestealthmodules;
		sectionid = builder.sectionid;
		sectionnumber = builder.sectionnumber;
		cmid = builder.cmid;
		modname = builder.modname;
		modid = builder.modid;
	}

	public Boolean getExcludemodules() {
		return excludemodules;
	}

	public void setExcludemodules(Boolean excludemodules) {
		this.excludemodules = excludemodules;
	}

	public Boolean getExcludecontents() {
		return excludecontents;
	}

	public void setExcludecontents(Boolean excludecontents) {
		this.excludecontents = excludecontents;
	}

	public Boolean getIncludestealthmodules() {
		return includestealthmodules;
	}

	public void setIncludestealthmodules(Boolean includestealthmodules) {
		this.includestealthmodules = includestealthmodules;
	}

	public Integer getSectionid() {
		return sectionid;
	}

	public void setSectionid(Integer sectionid) {
		this.sectionid = sectionid;
	}

	public Integer getSectionnumber() {
		return sectionnumber;
	}

	public void setSectionnumber(Integer sectionnumber) {
		this.sectionnumber = sectionnumber;
	}

	public Integer getCmid() {
		return cmid;
	}

	public void setCmid(Integer cmid) {
		this.cmid = cmid;
	}

	public String getModname() {
		return modname;
	}

	public void setModname(String modname) {
		this.modname = modname;
	}

	public Integer getModid() {
		return modid;
	}

	public void setModid(Integer modid) {
		this.modid = modid;
	}

	public Integer getCourseid() {
		return courseid;
	}

	public void setCourseid(Integer courseid) {
		this.courseid = courseid;
	}

	@Override
	public void addToMapParemeters() {
		Map<String, Object> map = new HashMap<>();
		parameters.put("courseid", Integer.toString(courseid));
		map.put("excludemodules", excludemodules);
		map.put("excludecontents", excludecontents);
		map.put("includestealthmodules", includestealthmodules);
		map.put("sectionid", sectionid);
		map.put("sectionnumber", sectionnumber);
		map.put("cmid", cmid);
		map.put("modname", modname);
		map.put("modid", modid);

		Util.putIfNotNull(parameters, "options", "name", "value", map);
	}

	/**
	 * Devuelve una nueva instancia para añadir parametros adicioneales.
	 * 
	 * @param courseid
	 *            id del curso
	 * @return builder
	 */
	public static Builder newBuilder(Integer courseid) {
		return new Builder(courseid);
	}

	/**
	 * Añade parametros adicionales a las request de Moodle.
	 * 
	 * @author Yi Peng Ji
	 *
	 */
	public static class Builder {

		private Integer courseid;
		private Boolean excludemodules;
		private Boolean excludecontents;
		private Boolean includestealthmodules;
		private Integer sectionid;
		private Integer sectionnumber;
		private Integer cmid;
		private String modname;
		private Integer modid;

		public Builder(Integer courseid) {
			this.courseid = courseid;

		}

		public Builder setExcludemodules(Boolean excludemodules) {
			this.excludemodules = excludemodules;
			return this;
		}

		public Builder setExcludecontents(Boolean excludecontents) {
			this.excludecontents = excludecontents;
			return this;
		}

		public Builder setIncludestealthmodules(Boolean includestealthmodules) {
			this.includestealthmodules = includestealthmodules;
			return this;
		}

		public Builder setSectionid(Integer sectionid) {
			this.sectionid = sectionid;
			return this;
		}

		public Builder setSectionnumber(Integer sectionnumber) {
			this.sectionnumber = sectionnumber;
			return this;
		}

		public Builder setCmid(Integer cmid) {
			this.cmid = cmid;
			return this;
		}

		public Builder setModname(String modname) {
			this.modname = modname;
			return this;
		}

		public Builder setModid(Integer modid) {
			this.modid = modid;
			return this;
		}

		public CoreCourseGetContents build() {
			return new CoreCourseGetContents(this);
		}

	}

}
