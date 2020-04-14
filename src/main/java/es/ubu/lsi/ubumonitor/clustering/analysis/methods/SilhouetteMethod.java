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

public class SilhouetteMethod extends AnalysisMethod {

	public SilhouetteMethod(Algorithm algorithm) {
		super(algorithm, Comparator.<Double>naturalOrder().reversed());
	}

	@Override
	protected double calculate(List<ClusterWrapper> clusters) {
		Map<UserData, Double> silhouette = silhouette(clusters, getDistance());
		OptionalDouble average = silhouette.values().stream().mapToDouble(Double::doubleValue).average();
		return average.getAsDouble();
	}

	public static Map<UserData, Double> silhouette(List<ClusterWrapper> clusters, Distance distanceType) {

		List<UserData> users = clusters.stream().flatMap(ClusterWrapper::stream).collect(Collectors.toList());
		Map<UserData, Double> ai = new HashMap<>(users.size());
		Map<UserData, Double> bi = new HashMap<>(users.size());
		DistanceMeasure distance = distanceType.getInstance();

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

		Map<UserData, Double> sihouette = new HashMap<>(users.size());
		for (UserData userData : users) {
			double a = ai.get(userData);
			double b = bi.getOrDefault(userData, Double.NaN);
			double s;
			if (Double.isNaN(b)) {
				s = 0.0;
			} else {
				s = (b - a) / Math.max(a, b);
			}
			sihouette.put(userData, s);
		}

		return sihouette;
	}

}
