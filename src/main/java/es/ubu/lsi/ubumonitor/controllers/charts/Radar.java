package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Radar extends ChartjsGradeItem {

	private static final GradeItem DUMMY = new GradeItem("");

	public Radar(MainController mainController) {
		super(mainController, ChartType.RADAR);
	
	}

	@Override
	public List<GradeItem> getSelectedGradeItems() {
		List<GradeItem> gradeItems = super.getSelectedGradeItems();
		for (int i = gradeItems.size(); i < 3; i++) {
			gradeItems.add(DUMMY);
		}
		return gradeItems;
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "radar");

		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		List<GradeItem> gradeItems = getSelectedGradeItems();
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}
		FileWriter out = new FileWriter(path);
		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				for (GradeItem gradeItem : gradeItems) {
					printer.print(gradeItem.getEnrolledUserPercentage(enrolledUser)/10);
				}
				printer.println();
			}
		}
	}

}
