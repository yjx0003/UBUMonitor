package es.ubu.lsi.ubumonitor.clustering.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;

public class ExportUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportUtil.class);

	private static void exportCSV(File file, String[] head, List<List<Object>> data) {
		CSVFormat format = CSVFormat.DEFAULT.withHeader(head);

		try (FileWriter out = new FileWriter(file);
			 CSVPrinter printer = new CSVPrinter(out, format)) {
			printer.printRecords(data);
		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", file);
			throw new IllegalStateException("Error exporting CSV file: " + file, e);
		}
	}

	public static void exportClustering(File file, List<ClusterWrapper> clusters, GradeItem... grades) {
		List<String> head = new ArrayList<>(Arrays.asList("UserId", "FullName", "Cluster"));
		if (!clusters.isEmpty()) {
			clusters.get(0).get(0).getData().forEach(d -> head.add(d.getItem()));
		}
		for (GradeItem gradeItem : grades) {
			head.add(gradeItem.getItemname());
		}

		List<List<Object>> data = new ArrayList<>();
		for (ClusterWrapper clusterWrapper : clusters) {
			for (UserData userData : clusterWrapper) {
				List<Object> row = new ArrayList<>();
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				row.add(enrolledUser.getId());
				row.add(enrolledUser.getFullName());
				row.add(clusterWrapper.getName());
				userData.getData().forEach(d -> row.add(d.getValue()));
				for (GradeItem gradeItem : grades) {
					row.add(gradeItem.getEnrolledUserPercentage(userData.getEnrolledUser()));
				}
				data.add(row);
			}
		}
		exportCSV(file, head.toArray(new String[0]), data);
	}

	public static void exportPoints(File file, List<Map<UserData, double[]>> points) {
		String[] head = new String[] { "UserId", "FullName", "Cluster", "X", "Y" };
		List<List<Object>> data = new ArrayList<>();
		for (Map<UserData, double[]> cluster : points) {
			for (Entry<UserData, double[]> entry : cluster.entrySet()) {
				UserData userData = entry.getKey();
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				List<Object> row = new ArrayList<>();
				row.add(enrolledUser.getId());
				row.add(enrolledUser.getFullName());
				row.add(userData.getCluster().getName());
				row.add(entry.getValue()[0]);
				row.add(entry.getValue()[1]);
				data.add(row);
			}
		}
		exportCSV(file, head, data);
	}

	public static void exportSilhouette(File file, Map<UserData, Double> silhouette) {
		String[] head = new String[] { "UserId", "FullName", "Cluster", "Silhouette width" };
		Comparator<UserData> id = Comparator.comparingInt(u -> u.getCluster().getId());
		Comparator<UserData> width = Comparator.comparingDouble((UserData u) -> silhouette.get(u)).reversed();
		List<UserData> usersData = silhouette.keySet().stream().sorted(id.thenComparing(width))
				.collect(Collectors.toList());

		List<List<Object>> data = new ArrayList<>();
		for (UserData userData : usersData) {
			EnrolledUser enrolledUser = userData.getEnrolledUser();
			List<Object> row = new ArrayList<>();
			row.add(enrolledUser.getId());
			row.add(enrolledUser.getFullName());
			row.add(userData.getCluster().getName());
			row.add(silhouette.get(userData));
			data.add(row);
		}
		exportCSV(file, head, data);
	}

}
