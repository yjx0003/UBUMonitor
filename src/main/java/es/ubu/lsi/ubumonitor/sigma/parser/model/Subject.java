package es.ubu.lsi.ubumonitor.sigma.parser.model;

import java.util.Objects;

public class Subject {
	private String code;
	private String name;
	private String type;
	
	public Subject(String code, String name, String type) {
		this.code = code;
		this.name = name;
		this.type = type;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Subject))
			return false;
		Subject other = (Subject) obj;
		return Objects.equals(code, other.code);
	}
	@Override
	public String toString() {
		return "Subject [code=" + code + ", name=" + name + ", type=" + type + "]";
	}
}
