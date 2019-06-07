package controllers.datasets;

import java.util.List;
import java.util.Map;

import controllers.I18n;
import model.ComponentEvent;
import model.EnrolledUser;

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
	 * @return instancia singleton
	 */
	public static StackedBarDataSetComponentEvent getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetComponentEvent();
		}
		return instance;
	}
	
	@Override
	protected String translate(ComponentEvent element) {
		return I18n.get(element.getComponent()) + " - "
				+ I18n.get("eventname." + element.getEventName());
	}

	@Override
	protected Map<ComponentEvent, List<Double>> getMeans() {
		return groupBy.getComponentEventMeans(selecteds, start, end);
	}

	@Override
	protected Map<EnrolledUser, Map<ComponentEvent, List<Long>>> getUserCounts() {
		return groupBy.getUserComponentEventCounts(selectedUsers, selecteds, start, end);
	}

}
