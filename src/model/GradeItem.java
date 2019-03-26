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

	private ItemType itemtype;
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

	public ItemType getItemtype() {
		return itemtype;
	}

	public void setItemtype(ItemType itemtype) {
		this.itemtype = itemtype;
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
		return graderaw.get(user);
	}

	public String getIconName() {
		return itemtype == ItemType.MOD ? itemModule.getModName() : itemtype.getItemTypeName();
	}

	@Override
	public String toString() {
		List<String> children = new ArrayList<String>();
		for (GradeItem g : this.children) {
			children.add(g.getItemname());
		}
		String father = this.father == null ? null : this.father.itemname;
		return "GradeItem [id=" + id + ", itemname=" + itemname + ", itemtype=" + itemtype + ", module=" + module
				+ ", weightraw=" + weightraw + ", graderaw=" + graderaw + ", grademin=" + grademin + ", grademax="
				+ grademax + ", course=" + course + ", father=" + father + ", children=" + children + ", level=" + level
				+ "]";
	}

	@Override
	public int hashCode() {
		return id;
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
		if (id != other.id)
			return false;
		return true;
	}

	public String getGradeAdjustedTo10(EnrolledUser  user) {
		double grade=getEnrolledUserGrade(user);
		if (Double.isNaN(grade)) {
			return "Nan";
		}
		//Normalizacion de la nota del alumno en rango [0,10]
		return Double.toString(((grade-grademin)/(grademax-grademin))*10);
	}

}
