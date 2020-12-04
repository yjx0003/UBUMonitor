package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.SilhouetteMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Datum;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

/**
 * Clase encargada de ejecutar un algoritmo de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public class AlgorithmExecuter {
	
	private Clusterer<UserData> clusterer;
	private Distance distance;
	private List<UserData> usersData;

	/**
	 * Constructor.
	 * 
	 * @param algorithm      algoritmo de clustering
	 * @param enrolledUsers  usuarios
	 * @param dataCollectors lista de colectores de datos
	 */
	public AlgorithmExecuter(Algorithm algorithm, List<EnrolledUser> enrolledUsers,
			List<DataCollector> dataCollectors) {
		this.clusterer = algorithm.getClusterer();
		this.distance = algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		if (usersData.size() < 2)
			throw new IllegalStateException("clustering.error.notUsers");
		dataCollectors.forEach(collector -> collector.collect(usersData));

	}

	private void filter(List<UserData> usersData) {
		List<Datum> data = usersData.get(0).getData();
		List<Integer> remove = new ArrayList<>();

		for (int j = 0; j < data.size(); j++) {
			if (checkData(usersData, data, j))
				remove.add(j);
		}
		if (remove.size() != data.size()) {
			for (UserData userData : usersData) {
				for (int i = remove.size() - 1; i >= 0; i--) {
					userData.removeDatum(remove.get(i));
				}
			}
		}
	}

	private boolean checkData(List<UserData> usersData, List<Datum> data, int j) {
		Number value = data.get(j).getValue();
		for (int i = 1; i < usersData.size(); i++) {
			if (!usersData.get(i).getData().get(j).getValue().equals(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Ejecuta una vez el algoritmo.
	 * 
	 * @param dim    reducir dimensiones, 0 para no reducir
	 * @param filter true para eliminar constantes
	 * @return lista de agrupaciones
	 */
	public List<ClusterWrapper> execute(int dim, boolean filter) {
		return execute(1, dim, filter);
	}

	/**
	 * Ejecuta el algoritmo varias veces y devuelve el mejor resultado.
	 * 
	 * @param iterations número de iteraciones
	 * @param dimension  reducir dimensiones, 0 para no reducir
	 * @param filter     true para eliminar constantes
	 * @return lista de agrupaciones
	 */
	public List<ClusterWrapper> execute(int iterations, int dimension, boolean filter) {
		
		if (usersData.size() < 2)
			throw new IllegalStateException("clustering.error.notUsers");

		if (usersData.get(0).getData().isEmpty())
			throw new IllegalStateException("clustering.error.notData");

		if (usersData.get(0).getData().size() < dimension)
			throw new IllegalStateException("clustering.error.invalidDimension");

		if (filter)
			filter(usersData);

		try {
			if (dimension > 0) {
				PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
				double[][] matrix = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
				matrix = pca.pca(matrix, dimension);
				for (int i = 0; i < matrix.length; i++) {
					usersData.get(i).setData(matrix[i]);
				}
			}

			List<ClusterWrapper> result = null;
			double best = 0.0;
			for (int count = 0; count < iterations; count++) {
				List<UserData> copy = usersData.stream().map(UserData::new).collect(Collectors.toList());
				List<? extends Cluster<UserData>> clusters = clusterer.cluster(copy);
				List<ClusterWrapper> users = new ArrayList<>();
				for (int i = 0; i < clusters.size(); i++) {
					ClusterWrapper clusterWrapper = new ClusterWrapper(i, clusters.get(i));
					for (UserData user : clusters.get(i).getPoints()) {
						user.setCluster(clusterWrapper);
					}
					users.add(clusterWrapper);
				}

				double mean = SilhouetteMethod.silhouette(users, distance).values().stream()
						.mapToDouble(Double::doubleValue).sum();
				if (mean >= best) {
					best = mean;
					result = users;
				}
			}

			return result;
		} catch (NumberIsTooSmallException e) {
			throw new IllegalStateException("clustering.error.lessUsersThanClusters", e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("clustering.error.invalidDimension");
		}
	}

	/**
	 * Devuelve el clusterer.
	 * 
	 * @return clusterer
	 */
	public Clusterer<UserData> getClusterer() {
		return clusterer;
	}

	/**
	 * Devuelve la lista de usuarios.
	 * 
	 * @return lista de usuarios
	 */
	public List<UserData> getUserData() {
		return usersData;
	}

	/**
	 * Reduce las dimensiones de los datos.
	 * 
	 * @param dim      número de dimensiones
	 * @param clusters lista de agrupaciones
	 * @return lista de mapas con clave el usuario y valor un array de dim
	 *         dimensiones
	 */
	public static List<Map<UserData, double[]>> clustersTo(int dim, List<ClusterWrapper> clusters) {
		if (clusters.isEmpty()) {
			return Collections.emptyList();
		}

		List<double[]> centers = new ArrayList<>();
		for (ClusterWrapper clusterWrapper : clusters) {
			double[] center = clusterWrapper.getCenter();
			if (center != null) {
				centers.add(center);
			}
		}

		double[][] matrix = Stream
				.concat(clusters.stream().flatMap(ClusterWrapper::stream).map(UserData::getPoint), centers.stream())
				.toArray(double[][]::new);

		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		List<Map<UserData, double[]>> points = new ArrayList<>();

		if (matrix[0].length > dim) {
			matrix = pca.pca(matrix, dim);
		}
		int i = 0;
		for (List<UserData> list : clusters) {
			Map<UserData, double[]> map = new LinkedHashMap<>();
			for (UserData userData : list) {
				map.put(userData, matrix[i++]);
			}
			points.add(map);
		}

		// Add centroides
		for (int j = 0; i < matrix.length; i++, j++) {
			points.get(j).put(null, matrix[i]);
		}

		return points;
	}

}
