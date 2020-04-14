package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

public class ElbowMethod extends AnalysisMethod {

	public ElbowMethod(Algorithm algorithm) {
		super(algorithm, Comparator.<Double>naturalOrder());
	}

	@Override
	protected double calculate(List<ClusterWrapper> clusters) {
		DistanceMeasure distance = getDistance().getInstance();
		double sum = 0.0;
		for (ClusterWrapper cluster : clusters) {
			double[] center = cluster.getCenter();
			for (UserData userData : cluster) {
				double[] point = userData.getPoint();
				sum += Math.pow(distance.compute(point, center), 2);
			}
		}
		return sum;
	}

}
