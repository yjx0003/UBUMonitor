package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 * @author Raúl Marticorena Sánchez
 *
 */
public class AlgorithmExecuter {
	
	private static final int MINUS_ONE = -1;
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
		
		// Only use the point data, not use centroids (corrected bug).
		double[][] matrix = clusters.stream().flatMap(ClusterWrapper::stream).map(UserData::getPoint)
				.toArray(double[][]::new);
		
		List<Map<UserData, double[]>> points = new ArrayList<>();			
		// PCA with T-SNE 	
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();		
		if (matrix[0].length > dim) {
			matrix = pca.pca(matrix, dim);
		}

		// Correct problem of twists with PCA projections
		changeSignInMatrixProjection(matrix, pca, dim); // fix problem of symmetry wit PCA
		
		// Add points
		int i = 0;
		for (List<UserData> list : clusters) {
			Map<UserData, double[]> map = new LinkedHashMap<>();
			for (UserData userData : list) {
				map.put(userData, matrix[i++]);
			}
			points.add(map);
		}		

		// We need to calculate the centroid projections in N dimensions...
		double[][] centroids = calculateCentroids(clusters, matrix);
		// Add centroids
		for (int j = 0; j < centroids.length; j++) {
			points.get(j).put(null, centroids[j]);
		}		

		return points;
	}
	
	/**
	 * Calculate the projections in N dimensions for each centroid.
	 * 
	 * @param clusters clusters
	 * @param matrix   data numerical points
	 * @return current projections of centroids
	 */
	private static double[][] calculateCentroids(final List<ClusterWrapper> clusters, final double[][] matrix){
		// number of clusters x number of dimensions after PCA
		double[][] centroids = new double[clusters.size()][matrix[0].length];		
		
		int position = 0;  // initial row position in data numerical matrix
		int currentNumberOfCentroid = 0;
		for (ClusterWrapper cluster : clusters) {
			double[] centroid = obtainCentroid(matrix, position, position + cluster.size());
			centroids[currentNumberOfCentroid++] = centroid; // add centroid 
			position += cluster.size(); // move forward to the next cluster row
		}		
		return centroids;
	}
	
	/**
	 * Obtain the projection in N dimensions for a cluster.
	 * 
	 * @param matrix data numerical points
	 * @param begin initial row in the cluster
	 * @param end	number of elements in the cluster
	 * @return centroid coordinates
	 */
	private static double[] obtainCentroid(double[][] matrix, final int begin, final int end) {
		int dimensions = matrix[0].length;
		double[] centroid = new double[dimensions];
		// add the x_i values in each dimension...
		for (int i = begin; i < end; i++) {
			for (int j = 0; j < dimensions; j++) {
				centroid[j] += matrix[i][j];
			}
		}
		// aritmetic mean for added values in each dimension
		for (int j = 0; j < dimensions; j++) {
			centroid[j] = centroid[j]/(end-begin);
		}
		return centroid;
	}
	
	/**
	 * Corrects the sign problem with PCA.
	 * 
	 * The PCA algorithm can give different solutions with the only different of the
	 * sign in the principal components. To correct the problem, we check the sign (of
	 * the first value in the principal component) and in the case of being negative,
	 * we change the corresponding sign in the projected column. In this way, it is 
	 * guaranteed that the projections are always the same (without twists).
	 * 
	 * Thanks to César Ignacio García Osorio and Juan José Rodriguez Diez.
	 * 
	 * @param matrix matrix
	 * @param pca pca
	 * @param numberOfComponents number of components
	 */
	private static void changeSignInMatrixProjection(double[][] matrix, final PrincipalComponentAnalysis pca, final int numberOfComponents) {
		for (int i = 0; i < numberOfComponents; i++) {
			// first element sign in i component
			boolean negativeComponent = pca.getBasisVector(i)[0] < 0 ? true : false; 
			if (negativeComponent) {
				changeSignInColumn(matrix, i); // Chaging sign in i-component (i-column)
			}
		}
	}
	
	/**
	 * Change sign in all elements in column.
	 * 
	 * @param matrix matrix
	 * @param column column to change sign
	 */
	private static void changeSignInColumn(double[][] matrix, final int column) {
		for (int i = 0; i < matrix.length; i++) {
			matrix[i][column] = matrix[i][column] * MINUS_ONE;
		}
	}
	
}
