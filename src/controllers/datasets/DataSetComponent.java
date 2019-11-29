package controllers.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import controllers.I18n;
import controllers.ubulogs.GroupByAbstract;
import model.Component;
import model.EnrolledUser;

/**
 * Clase que sobrescribe los metodos restantes para completar el equeleto de
 * {@link StackedBarDataSet}
 * 
 * @author Yi Peng Ji
 *
 */
public class DataSetComponent implements DataSet<Component> {

	/**
	 * static Singleton instance.
	 */
	private static DataSetComponent instance;

	/**
	 * Private constructor for singleton.
	 */
	private DataSetComponent() {
	}

	/**
	 * Return a singleton instance of StackedBarDataSetComponent.
	 * 
	 * @return la instancia única
	 */
	public static DataSetComponent getInstance() {
		if (instance == null) {
			instance = new DataSetComponent();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String translate(Component element) {
		return I18n.get(element);
	}

	@Override
	public Map<EnrolledUser, Map<Component, List<Long>>> getUserCounts(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<Component> elements, LocalDate start, LocalDate end) {
		return groupBy.getComponents().getUsersCounts(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<Component, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<Component> elements, LocalDate start, LocalDate end) {
		return groupBy.getComponents().getMeans(enrolledUsers, elements, start, end);

	}

}
