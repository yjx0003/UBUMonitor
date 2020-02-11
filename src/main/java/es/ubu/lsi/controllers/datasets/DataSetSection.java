package controllers.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;
import model.Section;

public class DataSetSection implements DataSet<Section> {
	
	/**
	 * Instacia única de la clase StackedBarDataSetSection.
	 */
	private static DataSetSection instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private DataSetSection() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDataSetSection.
	 * @return instancia singleton
	 */
	public static DataSetSection getInstance() {
		if (instance == null) {
			instance = new DataSetSection();
		}
		return instance;
	}

	@Override
	public String translate(Section element) {
		return element.getName();
	}


	@Override
	public Map<Section, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<Section> elements, LocalDate start, LocalDate end) {
		return groupBy.getSections().getMeans(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<EnrolledUser, Map<Section, List<Long>>> getUserCounts(GroupByAbstract<?> groupBy,
			List<EnrolledUser> enrolledUsers, List<Section> elements, LocalDate start, LocalDate end) {
		return groupBy.getSections().getUsersCounts(enrolledUsers, elements, start, end);
	}

}
