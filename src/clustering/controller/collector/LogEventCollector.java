package clustering.controller.collector;

import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetComponentEvent;
import model.ComponentEvent;

public class LogEventCollector extends LogCollector<ComponentEvent> {
	
	public LogEventCollector(MainController mainController) {
		super(mainController.getListViewEvents(), DataSetComponentEvent.getInstance());
	}
	
	@Override
	public String toString() {
		return I18n.get("tab.event");
	}

}
