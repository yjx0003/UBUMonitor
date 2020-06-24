package es.ubu.lsi.ubumonitor.clustering.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.Charsets;

/**
 * Clase de utilidad para la exportación en CSV.
 * 
 * @author Xing Long Ji
 *
 */
public class ExportUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportUtil.class);

	/**
	 * Exporta los datos a un fichero CSV.
	 * 
	 * @param file fichero
	 * @param head cabecera
	 * @param data datos
	 */
	public static void exportCSV(File file, String[] head, List<List<Object>> data) {
		Charsets charset = Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL,
				"charset");

		CSVFormat format = CSVFormat.DEFAULT.withHeader(head);

		try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), charset.get());
				CSVPrinter printer = new CSVPrinter(out, format)) {
			printer.printRecords(data);
		} catch (IOException e) {
			LOGGER.error("Error writing csv file: {}.csv", file);
			throw new IllegalStateException("Error exporting CSV file: " + file, e);
		}
	}

	/**
	 * Exporta el resultado del clustering a un fichero CSV.
	 * 
	 * @param file     fichero
	 * @param clusters lista de clusters
	 * @param grades   calificaciones a exportar
	 */
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

}
