package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.mod.Module;
import model.mod.ModuleType;

public class GradeItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Grade item id
	 */
	private int id;

	/**
	 * Grade item name
	 */
	private String itemname;


	private ModuleType itemModule;
	private Module module;
	private double weightraw;
	private Map<EnrolledUser, Double> graderaw;
	private double grademin;
	private double grademax;
	private Course course;

	private GradeItem father;
	private List<GradeItem> children;

	private int level;

	public GradeItem() {
		children = new ArrayList<GradeItem>();
		graderaw = new HashMap<EnrolledUser, Double>();
	}

	public GradeItem(int id) {
		this();
		this.id=id;
	}

	public GradeItem(String name) {
		this();
		this.itemname=name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}


	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public double getWeightraw() {
		return weightraw;
	}

	public void setWeightraw(double weightraw) {
		this.weightraw = weightraw;
	}

	public Map<EnrolledUser, Double> getGraderaw() {
		return graderaw;
	}

	public void setGraderaw(Map<EnrolledUser, Double> graderaw) {
		this.graderaw = graderaw;
	}

	public double getGrademin() {
		return grademin;
	}

	public void setGrademin(double grademin) {
		this.grademin = grademin;
	}

	public double getGrademax() {
		return grademax;
	}

	public void setGrademax(double grademax) {
		this.grademax = grademax;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public GradeItem getFather() {
		return father;
	}

	public void setFather(GradeItem father) {
		this.father = father;
	}

	public List<GradeItem> getChildren() {
		return children;
	}

	public void setChildren(List<GradeItem> children) {
		this.children = children;
	}

	public void addChildren(GradeItem children) {
		this.children.add(children);
	}
	
	public void clearChildren() {
		this.children.clear();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void addUserGrade(EnrolledUser enrolledUser, double grade) {
		graderaw.put(enrolledUser, grade);
		
	}

	public ModuleType getItemModule() {
		return itemModule;
	}

	public void setItemModule(ModuleType itemModule) {
		this.itemModule = itemModule;
	}

	public Collection<Double> getEnrolledUserGrades() {
		return graderaw.values();
	}

	public double getEnrolledUserGrade(EnrolledUser user) {
		return graderaw.getOrDefault(user,Double.NaN);
	}


	@Override
	public String toString() {
	return this.itemname;
	}


	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemname == null) ? 0 : itemname.hashCode());
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
		GradeItem other = (GradeItem) obj;
		if (itemname == null) {
			if (other.itemname != null)
				return false;
		} else if (!itemname.equals(other.itemname))
			return false;
		return true;
	}

	public double adjustTo10(EnrolledUser  user) {
		double grade=getEnrolledUserGrade(user);
		
		return adjustTo10(grade);
	}
	
	public double adjustTo10(double grade) {
		if (!Double.isNaN(grade)) {
			//Normalizacion de la nota del alumno en rango [0,10]
			return ((grade-grademin)/(grademax-grademin))*10;
			
		}
		
		return grade;
	}

}
