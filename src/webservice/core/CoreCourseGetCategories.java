package webservice.core;

import java.util.Set;
import java.util.stream.Collectors;

import webservice.WSFunctions;
import webservice.WebService;

public class CoreCourseGetCategories extends WebService{

	private String key;
	private Set<Integer> ids;
	
	public CoreCourseGetCategories(Set<Integer> ids) {
		key="ids";
	}
	
	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COURSE_GET_CATEGORIES;
	}

	@Override
	protected void appendToUrlParameters() {
		
		if(ids!=null && !ids.isEmpty()) {
			
			String commaSeparated=ids.stream()
					.map(i->i.toString())
					.collect(Collectors.joining(","));
			
			appendToUrlCriteria(key, commaSeparated);
		}
		
	}

}
