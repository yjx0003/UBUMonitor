package clustering.controller.collector;

import java.util.List;

import clustering.data.UserData;
import controllers.MainController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.GradeItem;

public class GradesCollector extends DataCollector {

	private TreeView<GradeItem> gradeItems;

	public GradesCollector(MainController mainController) {
		gradeItems = mainController.getTvwGradeReport();
	}

	@Override
	public void collect(List<UserData> users) {
		List<TreeItem<GradeItem>> selected = gradeItems.getSelectionModel().getSelectedItems();
		for (UserData userData : users) {
			for (TreeItem<GradeItem> treeItem : selected) {
				GradeItem gradeItem = treeItem.getValue();
				userData.addDatum(gradeItem.getItemname(),
						gradeItem.getEnrolledUserPercentage(userData.getEnrolledUser()));
			}
		}
	}
}
