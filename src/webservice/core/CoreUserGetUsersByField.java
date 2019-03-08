package webservice.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import webservice.MoodleOptions;
import webservice.WebService;

public class CoreUserGetUsersByField extends WebService {

	private Field field;
	private Set<String> values;

	public enum Field {
		id, idnumber, username, email;
	}

	private CoreUserGetUsersByField(Field field) {
		super(MoodleOptions.OBTENER_INFO_USUARIO);
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
	public String getResponse() throws IOException {

		if (field == null || values.size() == 0) {
			throw new IllegalStateException("Field no puede ser nulo y tiene que haber al menos un value");
		}

		String url = this.url;

		appendToUrlField(field);
		appendToUrlValues(values);
		

		return getContentWithJsoup(url);
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
