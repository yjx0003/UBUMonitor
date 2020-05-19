package es.ubu.lsi.ubumonitor.webservice.api.core.users;

import java.util.Arrays;
import java.util.Collection;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreUserGetUsersByField extends WSFunctionAbstract {

	/**
	 * the search field can be 'id' or 'idnumber' or 'username' or 'email'
	 */
	private Field field;

	private Collection<String> values;

	public enum Field {
		ID("id"), IDNUMBER("idnumber"), USERNAME("username"), EMAIL("email");

		private String fieldName;

		Field(String field) {
			this.fieldName = field;
		}

		@Override
		public String toString() {
			return fieldName;
		}
	}

	private CoreUserGetUsersByField() {
		super(WSFunctionEnum.CORE_USER_GET_USERS_BY_FIELD);
	}

	public CoreUserGetUsersByField(Field field, Collection<String> values) {
		this();
		this.field = field;
		this.values = values;
	}
	
	public CoreUserGetUsersByField(Field field, String ...values) {
		this(field, Arrays.asList(values));
	}

	@Override
	public void addToMapParemeters() {
		parameters.put("field", field.fieldName);
		Util.putIfNotNull(parameters, "values", values);
	}

}
