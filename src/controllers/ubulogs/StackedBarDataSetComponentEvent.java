package controllers.ubulogs;

import java.util.List;
import java.util.Map;

import controllers.ubulogs.logcreator.ComponentEvent;
import model.EnrolledUser;

public class StackedBarDataSetComponentEvent extends StackedBarDatasetAbstract<ComponentEvent> {

	/**
	 * static Singleton instance.
	 */
	private static StackedBarDataSetComponentEvent instance;

	/**
	 * Private constructor for singleton.
	 */
	private StackedBarDataSetComponentEvent() {
	}

	/**
	 * Return a singleton instance of StackedBarDataSetComponentEvent.
	 */
	public static StackedBarDataSetComponentEvent getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetComponentEvent();
		}
		return instance;
	}
	
	@Override
	protected String translate(ComponentEvent element) {
		return controller.getResourceBundle().getString("component." + element.getComponent()) + " - "
				+ controller.getResourceBundle().getString("eventname." + element.getEventName());
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
