package controllers.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;

public interface DataSet<T> {
	/**
	 * Traduce el elemento
	 * 
	 * @param element
	 *            elemento
	 * @return traducido
	 */
	String translate(T element);

	/**
	 * Devuelve un mapa con las medias.
	 * 
	 * @return las medias
	 */
	Map<T, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<T> elements, LocalDate start, LocalDate end);

	/**
	 * Devuele los conteos de los usuarios
	 * 
	 * @return los conteos de los usuarios
	 */
	Map<EnrolledUser, Map<T, List<Long>>> getUserCounts(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<T> elements, LocalDate start, LocalDate end);
}
