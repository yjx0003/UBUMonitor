package controllers.datasets;

import java.util.List;
import java.util.Map;

import model.EnrolledUser;
import model.Section;

public class StackedBarDataSetSection extends StackedBarDatasetAbstract<Section> {
	
	/**
	 * Instacia única de la clase StackedBarDataSetSection.
	 */
	private static StackedBarDataSetSection instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private StackedBarDataSetSection() {
	}

	/**
	 * Devuelve la instancia única de StackedBarDataSetSection.
	 * @return instancia singleton
	 */
	public static StackedBarDataSetSection getInstance() {
		if (instance == null) {
			instance = new StackedBarDataSetSection();
		}
		return instance;
	}

	@Override
	protected String translate(Section element) {
		return element.getName();
	}

	@Override
	protected Map<Section, List<Double>> getMeans() {
		return groupBy.getSections().getMeans(enrolledUsers, elements, start, end);
	}

	@Override
	protected Map<EnrolledUser, Map<Section, List<Long>>> getUserCounts() {
		return groupBy.getSections().getUsersCounts(enrolledUsers, elements, start, end);
	}

}
