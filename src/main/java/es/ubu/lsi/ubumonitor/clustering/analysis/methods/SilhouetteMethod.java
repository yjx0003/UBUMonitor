package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

/**
 * Método de la siluitea utiliazando el coeficiente de silueta.
 * 
 * @author Xing Long Ji
 *
 */
public class SilhouetteMethod extends AnalysisMethod {

	/**
	 * Constructor del método de la silueta.
	 * 
	 * @param algorithm algortimo de clustering
	 */
	public SilhouetteMethod(Algorithm algorithm) {
		super(algorithm, Comparator.<Double>naturalOrder().reversed());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculate(List<ClusterWrapper> clusters) {
		Map<UserData, Double> silhouette = silhouette(clusters, getDistance());
		OptionalDouble average = silhouette.values().stream().mapToDouble(Double::doubleValue).average();
		return average.getAsDouble();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getYLabel() {
		return "clustering.analyze.silhouette.yLabel";
	}

	/**
	 * Realiza el analisis a cada elemento de las agrupaciones.
	 * 
	 * @param clusters     lista de agrupaciones
	 * @param distanceType medida de distancia
	 * @return mapa con el valor del analisis de cada elemento
	 */
	public static Map<UserData, Double> silhouette(List<ClusterWrapper> clusters, Distance distanceType) {

		List<UserData> users = clusters.stream().flatMap(ClusterWrapper::stream).collect(Collectors.toList());
		Map<UserData, Double> ai = new HashMap<>(users.size());
		Map<UserData, Double> bi = new HashMap<>(users.size());
		DistanceMeasure distance = distanceType == null ? Distance.EUCLIDEAN_DISTANCE.getInstance()
				: distanceType.getInstance();

		for (ClusterWrapper cluster : clusters) {
			for (UserData userData : cluster) {
				double selfDissimilarity = 0.0;
				for (UserData userData2 : cluster) {
					selfDissimilarity += distance.compute(userData.getPoint(), userData2.getPoint());
				}
				selfDissimilarity /= cluster.size();
				ai.put(userData, selfDissimilarity);

				for (ClusterWrapper otherCluster : clusters) {
					if (!otherCluster.equals(userData.getCluster())) {
						double otherDissimilarity = 0.0;
						for (UserData userData3 : otherCluster) {
							otherDissimilarity += distance.compute(userData.getPoint(), userData3.getPoint());
						}
						otherDissimilarity /= otherCluster.size();
						if (otherDissimilarity < bi.getOrDefault(userData, Double.POSITIVE_INFINITY)) {
							bi.put(userData, otherDissimilarity);
						}
					}
				}
			}
		}

		Map<UserData, Double> silhouette = new HashMap<>(users.size());
		for (UserData userData : users) {
			double a = ai.get(userData);
			double b = bi.getOrDefault(userData, Double.NaN);
			double s;
			if (Double.isNaN(b)) {
				s = 0.0;
			} else {
				s = (b - a) / Math.max(a, b);
			}
			silhouette.put(userData, s);
		}
		return silhouette;
	}

}
