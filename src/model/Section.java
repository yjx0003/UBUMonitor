package model;

/**
 * Secciones de contenido de un curso donde se guardan los modulos del curso.
 * De momento no se usa está clase.
 * @author Yi Peng Ji
 *
 */
public class Section {
	
	/**
	 * Section ID
	 */
	private int id;
	
	/**
	 * Section name
	 */
	private String name;
	
	/**
	 * Optional.
	 * is the section visible
	 */
	private boolean visible;
	
	/**
	 * Section description
	 */
	private String summary;
	
	/**
	 * summary format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
	 */
	private int summaryformat;

	/**
	 * Optional.
	 * Section number inside the course
	 */
	private int sectionName;
	
	/**
	 * Optional.
	 * Whether is a section hidden in the course format
	 */
	private int hiddenbynumsections;
	/**
	 * Optional.
	 * Is the section visible for the user?
	 */
	private boolean uservisible; 
	
	/**
	 * Optional.
	 * Availability information.
	 */
	private String availabilityinfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getSummaryformat() {
		return summaryformat;
	}

	public void setSummaryformat(int summaryformat) {
		this.summaryformat = summaryformat;
	}

	public int getSectionName() {
		return sectionName;
	}

	public void setSectionName(int section) {
		this.sectionName = section;
	}

	public int getHiddenbynumsections() {
		return hiddenbynumsections;
	}

	public void setHiddenbynumsections(int hiddenbynumsections) {
		this.hiddenbynumsections = hiddenbynumsections;
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
	
}