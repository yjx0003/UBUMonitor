package controllers.datasets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import controllers.I18n;
import controllers.datasets.stackedbar.StackedBarDataSet;
import controllers.ubulogs.GroupByAbstract;
import model.ComponentEvent;
import model.EnrolledUser;

/**
 * Clase que sobrescribe los metodos restantes para completar el equeleto de {@link StackedBarDataSet}
 * @author Yi Peng Ji
 *
 */
public class DataSetComponentEvent implements DataSet<ComponentEvent> {

	/**
	 * Instacia única de la clase StackedBarDataSetComponentEvent
	 */
	private static DataSetComponentEvent instance;

	/**
	 * Constructor privado de la clase singleton
	 */
	private DataSetComponentEvent() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDataSetComponentEvent.
	 * 
	 * @return instancia singleton
	 */
	public static DataSetComponentEvent getInstance() {
		if (instance == null) {
			instance = new DataSetComponentEvent();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String translate(ComponentEvent element) {
		return I18n.get(element.getComponent()) + " - "
				+ I18n.get(element.getEventName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EnrolledUser, Map<ComponentEvent, List<Long>>> getUserCounts(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<ComponentEvent> elements, LocalDate start, LocalDate end) {
		return groupBy.getComponentsEvents().getUsersCounts(enrolledUsers, elements, start, end);
	}

	@Override
	public Map<ComponentEvent, List<Double>> getMeans(GroupByAbstract<?> groupBy, List<EnrolledUser> enrolledUsers,
			List<ComponentEvent> elements, LocalDate start, LocalDate end) {
		
		return groupBy.getComponentsEvents().getMeans(enrolledUsers, elements, start, end);
	}

}
