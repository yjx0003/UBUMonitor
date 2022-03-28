package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.sigma.controller.EnrolledUserStudentMapping;
import es.ubu.lsi.ubumonitor.sigma.model.AutonomousCommunity;
import es.ubu.lsi.ubumonitor.sigma.model.Province;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class SigmaUsualAddressMap extends Plotly {

	private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("(\\d{2})\\d{3} - .+$");
	private EnrolledUserStudentMapping enrolledUserStudentMapping;

	public SigmaUsualAddressMap(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.USUAL_ADDRESS_MAP);
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;

	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		Map<AutonomousCommunity, Map<Province, List<Student>>> map = getProvincesStudent(students);
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "username", "usualAddress", "zipCode", "province", "autonomousCommunity"))) {
			for (Map.Entry<AutonomousCommunity, Map<Province, List<Student>>> entry : map.entrySet()) {
				AutonomousCommunity autonomousCommunity = entry.getKey();
				Map<Province, List<Student>> mapProvince = entry.getValue();
				for (Map.Entry<Province, List<Student>> entryProvince : mapProvince.entrySet()) {
					Province province = entryProvince.getKey();
					List<Student> provinceStudents = entryProvince.getValue();
					for(Student student:provinceStudents) {
						EnrolledUser user = enrolledUserStudentMapping.getEnrolledUser(student);
						printer.print(user.getId());
						printer.print(user.getFullName());
						printer.print(student.getUsualAddress());
						printer.print(province.getZipCode());
						printer.print(province.getName());
						printer.print(I18n.get(autonomousCommunity.toString()));
						printer.println();
					}
				}
			}
			
		}

	}

	@Override
	public void createData(JSArray dataArray) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		Map<AutonomousCommunity, Map<Province, List<Student>>> map = getProvincesStudent(students);

		JSObject data = new JSObject();
		dataArray.add(data);
		data.put("type", "'treemap'");
		data.put("branchvalues", "'total'");

		if (!map.isEmpty()) {
			data.put("texttemplate",
					"'<b>%{label}</b><br>%{value}<br>%{percentParent} "
							+ UtilMethods.escapeJavaScriptText(I18n.get("autonomousCommunity")) + "<br>%{percentRoot} "
							+ UtilMethods.escapeJavaScriptText(I18n.get("spain")) + "'");
			data.put("hovertemplate",
					"'<b>%{label}</b><br>%{value}<br>%{percentParent:.2%} %{parent}<br>%{percentRoot:.2%} %{root}<extra></extra>'");
		}
		JSArray labels = createJSArray("labels", data);
		JSArray values = createJSArray("values", data);
		JSArray parents = createJSArray("parents", data);
		JSArray ids = createJSArray("ids", data);
		int total = 0;
		for (Map.Entry<AutonomousCommunity, Map<Province, List<Student>>> entry : map.entrySet()) {
			AutonomousCommunity autonomousCommunity = entry.getKey();
			Map<Province, List<Student>> mapProvince = entry.getValue();
			int communityTotal = 0;
			for (Map.Entry<Province, List<Student>> entryProvince : mapProvince.entrySet()) {
				Province province = entryProvince.getKey();
				int nStudents = entryProvince.getValue()
						.size();
				labels.addWithQuote(province.getName());
				values.add(nStudents);
				parents.addWithQuote(autonomousCommunity);
				ids.addWithQuote(province);
				communityTotal += nStudents;
				total += nStudents;
			}
			// community information
			labels.addWithQuote(I18n.get(autonomousCommunity.toString()));
			values.add(communityTotal);
			parents.add(0);
			ids.addWithQuote(autonomousCommunity);
		}
		// spain information
		labels.addWithQuote(I18n.get("spain"));
		values.add(total);
		parents.add("''");
		ids.add(0);

	}

	private JSArray createJSArray(String key, JSObject data) {
		JSArray jsArray = new JSArray();
		data.put(key, jsArray);
		return jsArray;
	}

	@Override
	public void createLayout(JSObject layout) {
		// do nothing

	}

	public static Map<AutonomousCommunity, Map<Province, List<Student>>> getProvincesStudent(List<Student> students) {
		Map<AutonomousCommunity, Map<Province, List<Student>>> map = new TreeMap<>(
				Comparator.comparing(a -> I18n.get(a.toString())));
		for (Student student : students) {
			String provinceZipCode = getProviceZipCode(student.getUsualAddress());
			Province province = Province.getProvinceByZipCode(provinceZipCode);

			Map<Province, List<Student>> provinceMap = map.computeIfAbsent(province.getAutonomousCommunity(),
					k -> new TreeMap<>(Comparator.comparing(Province::getName)));

			List<Student> studentsInProvince = provinceMap.computeIfAbsent(province, k -> new ArrayList<>());
			studentsInProvince.add(student);
		}

		return map;
	}

	public static String getProviceZipCode(String address) {
		Matcher matcher = ZIP_CODE_PATTERN.matcher(address);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "-";
	}

}
