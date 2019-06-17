package webservice.core;

import java.util.Set;
import java.util.stream.Collectors;

import webservice.WSFunctions;
import webservice.WebService;
/**
 * Función que devuelve la información de las categorias de los cursos.
 * De momento solo funciona con parametros ids.
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetCategories extends WebService{

	private String key;
	private Set<Integer> ids;
	
	/**
	 * Constructor con los ids de las categorias que se quiere buscar info.
	 * @param ids conjunto de ids que se quiere buscar info
	 */
	public CoreCourseGetCategories(Set<Integer> ids) {
		key="ids";
		this.ids=ids;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COURSE_GET_CATEGORIES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToUrlParameters() {
		
		if(ids!=null && !ids.isEmpty()) {
			
			String commaSeparated=ids.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
			
			appendToUrlCriteria(key, commaSeparated);
		}
		
	}

}
