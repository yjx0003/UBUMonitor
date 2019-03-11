package webservice.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import webservice.WSFunctions;
import webservice.WebService;

public class CoreUserGetUsersByField extends WebService {

	private Field field;
	private Set<String> values;

	public enum Field {
		ID("id"), IDNUMBER("idnumber"), USERNAME("username"), EMAIL("email");
		
		private String field;
		Field(String field){
			this.field=field;
		}
		@Override
		public String toString() {
			return field;
		}
	}

	private CoreUserGetUsersByField(Field field) {
		this.field = field;
		
	}

	public CoreUserGetUsersByField(Field field, String... values) {
		this(field);
		this.values = new HashSet<String>(Arrays.asList(values));

	}

	public CoreUserGetUsersByField(Field field, Set<String> values) {
		this(field);
		this.values = values;

	}
	@Override
	public WSFunctions getWSFunction() {
		
		return WSFunctions.CORE_USER_GET_USERS_BY_FIELD;
	}
	
	@Override
	public void appendToUrlParameters() {
		if (field == null || values.size() == 0) {
			throw new IllegalStateException("Field no puede ser nulo y tiene que haber al menos un value");
		}

		appendToUrlField(field);
		appendToUrlValues(values);
		
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}
	
	public void addValue(String value) {
		this.values.add(value);
	}
	
	public void removeValue(String value) {
		this.values.remove(value);
	}







}
