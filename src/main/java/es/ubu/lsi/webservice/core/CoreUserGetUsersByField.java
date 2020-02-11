package es.ubu.lsi.webservice.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import es.ubu.lsi.webservice.WSFunctions;
import es.ubu.lsi.webservice.WebService;

/**
 * Devuelve los usuarios de Moodle en función del campo (id,username,email,etc).
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreUserGetUsersByField extends WebService {
	private Field field;
	private Set<String> values;

	/**
	 * Enumeración que indica la forma que se busca un usuario.
	 * 
	 * @author Yi Peng Ji
	 *
	 */
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

	/**
	 * Constructor que indica el field por los que se buscan los usuarios y un
	 * número indeterminado de valores.
	 * 
	 * @param field
	 *            field
	 * @param values
	 *            valores del field
	 */
	public CoreUserGetUsersByField(Field field, String... values) {
		this.field = field;
		this.values = new HashSet<>(Arrays.asList(values));

	}

	/**
	 * Constructor que indica el field por los que se buscan los usuarios y un
	 * conjunto de valores.
	 * 
	 * @param field field
	 * @param values values
	 */
	public CoreUserGetUsersByField(Field field, Set<String> values) {
		this.field = field;
		this.values = values;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {

		return WSFunctions.CORE_USER_GET_USERS_BY_FIELD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToUrlParameters() {
		if (field == null || values.isEmpty()) {
			throw new IllegalStateException("Field no puede ser nulo y tiene que haber al menos un value");
		}

		appendToUrlField(field);
		appendToUrlValues(values);

	}

}
