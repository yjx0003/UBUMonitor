package es.ubu.lsi.ubumonitor.model.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;

public interface DataSet<E> {
	/**
	 * Traduce el elemento
	 * 
	 * @param element
	 *            elemento
	 * @return traducido
	 */
	String translate(E element);

	/**
	 * Devuelve un mapa con las medias.
	 * 
	 * @return las medias
	 */
	Map<E, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<E> elements, LocalDate start, LocalDate end);

	/**
	 * Devuele los conteos de los usuarios
	 * 
	 * @return los conteos de los usuarios
	 */
	Map<EnrolledUser, Map<E, List<Integer>>> getUserCounts(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<E> elements, LocalDate start, LocalDate end);
	
	Map<EnrolledUser, Map<E, List<LogLine>>> getUserLogs(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<E> elements, LocalDate start, LocalDate end);
	
	Map<EnrolledUser, Integer> getUserTotalLogs(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<E> elements, LocalDate start, LocalDate end);
}
