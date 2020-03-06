package clustering.controller.collector;

import java.util.List;

import clustering.data.Datum;
import clustering.data.UserData;
import controllers.MainController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.GradeItem;

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
				userData.addDatum(
						new Datum(getType(), gradeItem.getItemname(), gradeItem.getItemModule().getModName(), value));
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
