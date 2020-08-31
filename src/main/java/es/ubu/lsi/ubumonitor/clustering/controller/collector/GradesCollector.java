package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Clase que recoge las calificaciones de los alumnos.
 * @author Xing Long Ji
 *
 */
public class GradesCollector extends DataCollector {

	private TreeView<GradeItem> gradeItems;

	/**
	 * Constructor.
	 * @param mainController controlador general de UBUMonitor
	 */
	public GradesCollector(MainController mainController) {
		super("clustering.type.grade");
		gradeItems = mainController.getSelectionController().getTvwGradeReport();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collect(List<UserData> users) {
		List<TreeItem<GradeItem>> selected = gradeItems.getSelectionModel().getSelectedItems();
		for (UserData userData : users) {
			for (TreeItem<GradeItem> treeItem : selected) {
				GradeItem gradeItem = treeItem.getValue();
				double value = gradeItem.getEnrolledUserPercentage(userData.getEnrolledUser());
				String iconFile = gradeItem.getItemModule() == null ? null : gradeItem.getItemModule().getModName();
				userData.addDatum(new Datum(getType(), gradeItem.getItemname(), iconFile, value));
				
				double nomalized = Double.isNaN(value) ? 0.0 : value / 100.0;
				userData.addNormalizedDatum(nomalized);
			}
		}
	}
}
