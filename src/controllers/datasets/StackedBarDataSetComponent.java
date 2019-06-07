package controllers.datasets;

import java.util.List;
import java.util.Map;

import controllers.I18n;
import model.Component;
import model.EnrolledUser;

public class StackedBarDataSetComponent extends StackedBarDatasetAbstract<Component>{

	/**
	 * static Singleton instance.
	 */
	private static StackedBarDataSetComponent instance;

	/**
	 * Private constructor for singleton.
	 */
	private StackedBarDataSetComponent() {
	}

	/**
	 * Return a singleton instance of StackedBarDataSetComponent.
	 * @return la instancia única
	 */
	public static StackedBarDataSetComponent getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetComponent();
		}
		return instance;
	}
	
	@Override
	protected String translate(Component element) {
		return I18n.get(element);
	}

	@Override
	protected Map<Component, List<Double>> getMeans() {
		return groupBy.getComponentsMeans(selecteds, start, end);
	}

	@Override
	protected Map<EnrolledUser, Map<Component, List<Long>>> getUserCounts() {
		return groupBy.getUsersComponentCounts(selectedUsers, selecteds, start, end);
	}

}
