package clustering.controller.collector;

import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetComponent;
import model.Component;

public class LogComponentCollector extends LogCollector<Component> {

	public LogComponentCollector(MainController mainController) {
		super(mainController.getListViewComponents(), DataSetComponent.getInstance());
	}

	@Override
	public String toString() {
		return I18n.get("tab.component");
	}

}
