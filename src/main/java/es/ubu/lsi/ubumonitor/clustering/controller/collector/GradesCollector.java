package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import es.ubu.lsi.ubumonitor.model.GradeItem;

public class GradesCollector extends DataCollector {

	private TreeView<GradeItem> gradeItems;

	public GradesCollector(MainController mainController) {
		super("clustering.type.grade");
		gradeItems = mainController.getTvwGradeReport();
	}

	@Override
	public void collect(List<UserData> users) {
		List<TreeItem<GradeItem>> selected = gradeItems.getSelectionModel().getSelectedItems();
		for (UserData userData : users) {
			for (TreeItem<GradeItem> treeItem : selected) {
				GradeItem gradeItem = treeItem.getValue();
				double value = gradeItem.getEnrolledUserPercentage(userData.getEnrolledUser());
				String iconFile = gradeItem.getItemModule() == null ? null : gradeItem.getItemModule().getModName();
				userData.addDatum(new Datum(getType(), gradeItem.getItemname(), iconFile, value));
				if (Double.isNaN(value)) {
					value = 0.0;
				} else {
					value /= 100.0;
				}
				userData.addNormalizedDatum(value);
			}
		}
	}
}
