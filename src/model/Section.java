package model;

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
	private int section;
	
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
	
}