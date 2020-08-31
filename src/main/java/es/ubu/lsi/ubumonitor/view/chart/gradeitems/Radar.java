package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.scene.control.TreeView;

public class Radar extends ChartjsGradeItem {

	private static final GradeItem DUMMY = new GradeItem(-1);

	public Radar(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.RADAR, treeViewGradeItem);
	
	}

	@Override
	public List<GradeItem> getSelectedGradeItems(TreeView<GradeItem> treeViewGradeItem) {
		List<GradeItem> gradeItems = super.getSelectedGradeItems(treeViewGradeItem);
		for (int i = gradeItems.size(); i < 3; i++) {
			gradeItems.add(DUMMY);
		}
		return gradeItems;
	}

	@Override
	public String getOptions(JSObject jsObject) {
		jsObject.putWithQuote("typeGraph", "radar");
		jsObject.put("scale", "{ticks:{max:10,stepSize:1}}");
		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}
	
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				for (GradeItem gradeItem : gradeItems) {
					printer.print(adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser)));
				}
				printer.println();
			}
		}
	}

}
