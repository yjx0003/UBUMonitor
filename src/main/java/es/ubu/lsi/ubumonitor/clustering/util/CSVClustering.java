package es.ubu.lsi.ubumonitor.clustering.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

public class CSVClustering {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVClustering.class);

	private static final String[] HEAD_TABLE = new String[] { "UserId", "FullName", "Cluster" };

	private static final String[] HEAD_POINTS = new String[] { "UserId", "FullName", "Cluster", "X", "Y" };
	
	private static final String[] HEAD_SILHOUETTE = new String[] { "UserId", "FullName", "Cluster", "Silhouette width" };

	public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	public static void exportTable(List<ClusterWrapper> clusters, Path path) {
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				CSVWriter csvWriter = new CSVWriter(writer)) {

			if (!clusters.isEmpty()) {
				String[] columns = clusters.get(0).get(0).getData().stream().map(Datum::getItem).toArray(String[]::new);
				String[] head = new String[HEAD_TABLE.length + columns.length];
				System.arraycopy(HEAD_TABLE, 0, head, 0, HEAD_TABLE.length);
				System.arraycopy(columns, 0, head, HEAD_TABLE.length, columns.length);

				csvWriter.writeNext(head);
			} else {
				csvWriter.writeNext(HEAD_TABLE);
			}

			for (ClusterWrapper clusterWrapper : clusters) {
				for (UserData userData : clusterWrapper) {
					EnrolledUser enrolledUser = userData.getEnrolledUser();
					List<String> data = new ArrayList<>();
					data.add(String.valueOf(enrolledUser.getId()));
					data.add(enrolledUser.getFullName());
					data.add(clusterWrapper.getName());
					userData.getData().forEach(d -> data.add(String.valueOf(d.getValue())));
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
					csvWriter.writeNext(new String[] {
							String.valueOf(enrolledUser.getId()),
							enrolledUser.getFullName(),
							userData.getCluster().getName(),
							String.valueOf(entry.getValue()[0]),
							String.valueOf(entry.getValue()[1])
					});
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
		List<UserData> usersData = silhouette.keySet().stream().sorted(id.thenComparing(width)).collect(Collectors.toList());
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
				CSVWriter csvWriter = new CSVWriter(writer)) {

			csvWriter.writeNext(HEAD_SILHOUETTE);
			for (UserData userData : usersData) {
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				csvWriter.writeNext(new String[] {
						String.valueOf(enrolledUser.getId()),
						enrolledUser.getFullName(),
						userData.getCluster().getName(),
						String.valueOf(silhouette.get(userData)),
				});
			}

		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", path);
			throw new IllegalStateException("Error exporting CSV file" + path, e);
		}
	}
}
