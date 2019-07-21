package controllers.datasets;

import java.util.List;
import java.util.Map;

import controllers.I18n;
import model.ComponentEvent;
import model.EnrolledUser;

/**
 * Clase que sobrescribe los metodos restantes para completar el equeleto de {@link StackedBarDatasetAbstract}
 * @author Yi Peng Ji
 *
 */
public class StackedBarDataSetComponentEvent extends StackedBarDatasetAbstract<ComponentEvent> {

	/**
	 * Instacia única de la clase StackedBarDataSetComponentEvent
	 */
	private static StackedBarDataSetComponentEvent instance;

	/**
	 * Constructor privado de la clase singleton
	 */
	private StackedBarDataSetComponentEvent() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDataSetComponentEvent.
	 * 
	 * @return instancia singleton
	 */
	public static StackedBarDataSetComponentEvent getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetComponentEvent();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String translate(ComponentEvent element) {
		return I18n.get(element.getComponent()) + " - "
				+ I18n.get("eventname." + element.getEventName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<ComponentEvent, List<Double>> getMeans() {
		return groupBy.getComponentsEvents().getMeans(enrolledUsers, elements, start, end);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<EnrolledUser, Map<ComponentEvent, List<Long>>> getUserCounts() {
		return groupBy.getComponentsEvents().getUsersCounts(selectedUsers, elements, start, end);
	}

}
