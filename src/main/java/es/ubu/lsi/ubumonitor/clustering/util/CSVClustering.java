package es.ubu.lsi.ubumonitor.clustering.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CSVClustering {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVClustering.class);

	private static final List<String> HEAD_TABLE = Arrays.asList("UserId", "FullName", "Cluster");

	private static final String[] HEAD_POINTS = new String[] { "UserId", "FullName", "Cluster", "X", "Y" };

	private static final String[] HEAD_SILHOUETTE = new String[] { "UserId", "FullName", "Cluster",
			"Silhouette width" };

	public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	public static void exportTable(List<ClusterWrapper> clusters, Path path) {
		exportTable(clusters, path, null);
	}

	public static void exportTable(List<ClusterWrapper> clusters, Path path, TableView<UserData> tableView) {
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				CSVWriter csvWriter = new CSVWriter(writer)) {

			if (clusters.isEmpty()) {
				csvWriter.writeNext(HEAD_TABLE.toArray(new String[0]));
			} else {
				List<String> head = new ArrayList<String>(HEAD_TABLE);
				List<String> columns = clusters.get(0).get(0).getData().stream().map(Datum::getItem)
						.collect(Collectors.toList());
				head.addAll(columns);

				if (tableView != null) {
					List<String> grades = tableView.getColumns().stream().map(TableColumn::getText)
							.collect(Collectors.toList());
					head.addAll(grades.subList(3, grades.size()));
				}
				csvWriter.writeNext(head.toArray(new String[0]));

			}

			for (ClusterWrapper clusterWrapper : clusters) {
				for (UserData userData : clusterWrapper) {
					EnrolledUser enrolledUser = userData.getEnrolledUser();
					List<String> data = new ArrayList<>();
					data.add(String.valueOf(enrolledUser.getId()));
					data.add(enrolledUser.getFullName());
					data.add(clusterWrapper.getName());
					userData.getData().forEach(d -> data.add(String.valueOf(d.getValue())));
					if (tableView != null) {
						List<TableColumn<UserData, ?>> columns = tableView.getColumns();
						for (TableColumn<UserData, ?> column : columns.subList(3, columns.size())) {
							data.add(column.getCellData(userData).toString());
						}
					}
					csvWriter.writeNext(data.toArray(new String[0]));
				}
			}

		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", path);
			throw new IllegalStateException("Error exporting CSV file" + path, e);
		}
	}

	public static void exportPoints(List<Map<UserData, double[]>> points, Path path) {
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				CSVWriter csvWriter = new CSVWriter(writer)) {

			csvWriter.writeNext(HEAD_POINTS);
			for (Map<UserData, double[]> cluster : points) {
				for (Entry<UserData, double[]> entry : cluster.entrySet()) {
					UserData userData = entry.getKey();
					EnrolledUser enrolledUser = userData.getEnrolledUser();
					csvWriter.writeNext(new String[] { String.valueOf(enrolledUser.getId()), enrolledUser.getFullName(),
							userData.getCluster().getName(), String.valueOf(entry.getValue()[0]),
							String.valueOf(entry.getValue()[1]) });
				}
			}

		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", path);
			throw new IllegalStateException("Error exporting CSV file" + path, e);
		}
	}

	public static void exportSilhouette(Map<UserData, Double> silhouette, Path path) {
		Comparator<UserData> id = Comparator.comparingInt(u -> u.getCluster().getId());
		Comparator<UserData> width = Comparator.comparingDouble((UserData u) -> silhouette.get(u)).reversed();
		List<UserData> usersData = silhouette.keySet().stream().sorted(id.thenComparing(width))
				.collect(Collectors.toList());
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				CSVWriter csvWriter = new CSVWriter(writer)) {

			csvWriter.writeNext(HEAD_SILHOUETTE);
			for (UserData userData : usersData) {
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				csvWriter.writeNext(new String[] { String.valueOf(enrolledUser.getId()), enrolledUser.getFullName(),
						userData.getCluster().getName(), String.valueOf(silhouette.get(userData)), });
			}

		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", path);
			throw new IllegalStateException("Error exporting CSV file" + path, e);
		}
	}

}
