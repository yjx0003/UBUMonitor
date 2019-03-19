package webservice.core;

import webservice.ParametersCourseid;
import webservice.WSFunctions;

public class CoreCourseGetContents extends ParametersCourseid {
	/**
	 * Do not return modules, return only the sections structure
	 */
	private boolean excludemodules;

	/**
	 * Do not return module contents (i.e: files inside a resource)
	 */
	private boolean excludecontents;

	/**
	 * Return stealth modules for students in a special section (with id -1)
	 */
	private boolean includestealthmodules;

	/**
	 * Return only this section
	 */
	private int sectionid;

	/**
	 * Return only this section with number (order)
	 */
	private int sectionnumber;

	/**
	 * Return only this module information (among the whole sections structure)
	 */
	private int cmid;

	/**
	 * Return only modules with this name "label, forum, etc..."
	 */
	private String modname;

	/**
	 * Return only the module with this id (to be used with modname)
	 */
	private int modid;

	private int index = 0;

	public CoreCourseGetContents(int courseid) {
		super(courseid);

	}

	private CoreCourseGetContents(Builder builder) {
		super(builder.courseid);

		excludemodules = builder.excludemodules;
		excludecontents = builder.excludecontents;
		includestealthmodules = builder.includestealthmodules;
		sectionid = builder.sectionid;
		sectionnumber = builder.sectionnumber;
		cmid = builder.cmid;
		modname = builder.modname;
		modid = builder.modid;

	}

	@Override
	public void appendToUrlParameters() {

		if (modid > 0 && modname != null)
			throw new IllegalStateException("Hay que asignar modname si se asigna modid");

		super.appendToUrlParameters();
		appendIfPossible("excludemodules", excludemodules);
		appendIfPossible("excludecontents", excludecontents);
		appendIfPossible("includestealthmodules", includestealthmodules);
		appendIfPossible("sectionid", sectionid);
		appendIfPossible("sectionnumber", sectionnumber);
		appendIfPossible("cmid", cmid);
		appendIfPossible("modname", modname);
		appendIfPossible("modid", modid);

	}

	private void appendIfPossible(String name, String value) {
		if (value != null)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfPossible(String name, int value) {
		if (value != 0)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfPossible(String name, boolean value) {
		if (value)
			appendToUrlOptions(index++, name, value);
	}

	@Override
	public WSFunctions getWSFunction() {

		return WSFunctions.CORE_COURSE_GET_CONTENTS;
	}

	public boolean isExcludemodules() {
		return excludemodules;
	}

	public void setExcludemodules(boolean excludemodules) {
		this.excludemodules = excludemodules;
	}

	public boolean isExcludecontents() {
		return excludecontents;
	}

	public void setExcludecontents(boolean excludecontents) {
		this.excludecontents = excludecontents;
	}

	public boolean isIncludestealthmodules() {
		return includestealthmodules;
	}

	public void setIncludestealthmodules(boolean includestealthmodules) {
		this.includestealthmodules = includestealthmodules;
	}

	public int getSectionid() {
		return sectionid;
	}

	public void setSectionid(int sectionid) {
		this.sectionid = sectionid;
	}

	public int getSectionnumber() {
		return sectionnumber;
	}

	public void setSectionnumber(int sectionnumber) {
		this.sectionnumber = sectionnumber;
	}

	public int getCmid() {
		return cmid;
	}

	public void setCmid(int cmid) {
		this.cmid = cmid;
	}

	public String getModname() {
		return modname;
	}

	public void setModname(String modname) {
		this.modname = modname;
	}

	public int getModid() {
		return modid;
	}

	public void setModid(int modid) {
		this.modid = modid;
	}
	
	public static Builder newBuilder(int courseid) {
		return new Builder(courseid);
	}

	public static class Builder {

		private int courseid;
		private boolean excludemodules;
		private boolean excludecontents;
		private boolean includestealthmodules;
		private int sectionid;
		private int sectionnumber;
		private int cmid;
		private String modname;
		private int modid;

		public Builder(int courseid) {
			this.courseid = courseid;
			
		}

		public Builder setExcludemodules(boolean excludemodules) {
			this.excludemodules = excludemodules;
			return this;
		}

		public Builder setExcludecontents(boolean excludecontents) {
			this.excludecontents = excludecontents;
			return this;
		}

		public Builder setIncludestealthmodules(boolean includestealthmodules) {
			this.includestealthmodules = includestealthmodules;
			return this;
		}

		public Builder setSectionid(int sectionid) {
			this.sectionid = sectionid;
			return this;
		}

		public Builder setSectionnumber(int sectionnumber) {
			this.sectionnumber = sectionnumber;
			return this;
		}

		public Builder setCmid(int cmid) {
			this.cmid = cmid;
			return this;
		}

		public Builder setModname(String modname) {
			this.modname = modname;
			return this;
		}

		public Builder setModid(int modid) {
			this.modid = modid;
			return this;
		}

		public CoreCourseGetContents build() {
			return new CoreCourseGetContents(this);
		}

	}

}
