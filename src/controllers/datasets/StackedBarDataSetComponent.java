package controllers.datasets;

import java.util.List;
import java.util.Map;

import controllers.I18n;
import model.Component;
import model.EnrolledUser;


/**
 * Clase que sobrescribe los metodos restantes para completar el equeleto de {@link StackedBarDatasetAbstract}
 * @author Yi Peng Ji
 *
 */
public class StackedBarDataSetComponent extends StackedBarDatasetAbstract<Component> {

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
	 * 
	 * @return la instancia única
	 */
	public static StackedBarDataSetComponent getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetComponent();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String translate(Component element) {
		return I18n.get(element);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<Component, List<Double>> getMeans() {
		return groupBy.getComponentsMeans(enrolledUsers, selecteds, start, end);
	}

	@Override
	protected Map<EnrolledUser, Map<Component, List<Long>>> getUserCounts() {
		return groupBy.getUsersComponentCounts(selectedUsers, selecteds, start, end);
	}

}
