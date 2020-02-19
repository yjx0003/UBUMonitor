package clustering.controller.collector;

import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetSection;
import model.Section;

public class LogSectionCollector extends LogCollector<Section> {

	public LogSectionCollector(MainController mainController) {
		super(mainController.getListViewSection(), DataSetSection.getInstance());
	}

	@Override
	public String toString() {
		return I18n.get("tab.section");
	}

}
