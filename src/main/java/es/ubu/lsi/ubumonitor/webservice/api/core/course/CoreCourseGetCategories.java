package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Collection;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Return course category details
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetCategories extends WSFunctionAbstract {

	private JSONArray criteria;

	public CoreCourseGetCategories() {
		super(WSFunctionEnum.CORE_COURSE_GET_CATEGORIES);
		criteria = new JSONArray();
	}

	private void append(String key, Object value) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key", key);
		jsonObject.put("value", value);
		criteria.put(jsonObject);
	}

	/**
	 * The category column to search (id).
	 * 
	 * @param id The category id
	 */
	public void appendId(int id) {
		append("id", id);
	}

	/**
	 * The category column to search (ids).
	 * 
	 * @param ids Tategory ids separated by commas
	 */
	public void appendIds(Collection<Integer> ids) {
		if (ids != null && !ids.isEmpty()) {
			append("ids", ids.stream()
					.map(Object::toString)
					.collect(Collectors.joining(",")));
		}

	}

	/**
	 * The category column to search (name).
	 * 
	 * @param name The category name
	 */
	public void appendName(String name) {
		append("name", name);
	}

	/**
	 * The category column to search (parent).
	 * 
	 * @param parent The parent category id
	 */
	public void appendParent(int parent) {
		append("parent", parent);
	}

	/**
	 * The category column to search (idnumber).
	 * 
	 * @param idnumber Category idnumber - user must have 'moodle/category:manage'
	 *                 to search on idnumber
	 */
	public void appendIdnumber(int idnumber) {
		append("idnumber", idnumber);
	}

	/**
	 * The category column to search (visible).
	 * 
	 * @param visible Whether the returned categories must be visible or hidden. If
	 *                the key is not passed, then the function return all categories
	 *                that the user can see. - user must have
	 *                'moodle/category:manage' or
	 *                'moodle/category:viewhiddencategories' to search on visible
	 */
	public void appendVisible(boolean visible) {
		append("visible", visible ? 1 : 0);
	}

	/**
	 * The category column to search (theme).
	 * 
	 * @param theme Only return the categories having this theme - user must have
	 *              'moodle/category:manage' to search on theme
	 */
	public void appendTheme(String theme) {
		append("theme", theme);
	}

	/**
	 * Return the sub categories infos.
	 * 
	 * @param addsubcategories (1 - default) otherwise only the category info (0)
	 */
	public void setAddsubcategories(boolean addsubcategories) {
		parameters.put("addsubcategories", addsubcategories ? 1 : 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addToMapParemeters() {
		if (!criteria.isEmpty()) {
			parameters.put("criteria", criteria);
		}
	}

}
