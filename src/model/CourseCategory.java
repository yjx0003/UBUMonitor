package model;

import java.io.Serializable;

public class CourseCategory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int id;

	/**
	 * category name
	 */
	String name;

	/**
	 * category description
	 */
	String description;

	/**
	 * description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
	 */
	DescriptionFormat descriptionFormat;

	/**
	 * parent category id
	 */
	int parent;

	/**
	 * category sorting order
	 */
	int sortorder;

	/**
	 * number of courses in this category
	 */
	int coursecount;

	/**
	 * category depth
	 */
	int depth;
	/**
	 * category path
	 */
	String path;

	public CourseCategory(int id) {
		this.id = id;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DescriptionFormat getDescriptionFormat() {
		return descriptionFormat;
	}

	public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getSortorder() {
		return sortorder;
	}

	public void setSortorder(int sortorder) {
		this.sortorder = sortorder;
	}

	public int getCoursecount() {
		return coursecount;
	}

	public void setCoursecount(int coursecount) {
		this.coursecount = coursecount;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseCategory other = (CourseCategory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
