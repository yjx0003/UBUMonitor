package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

/**
 * Metedo del codo utilizando la suma de las varianzas dentro de cada
 * agrupación.
 * 
 * @author Xing Long Ji
 *
 */
public class ElbowMethod extends AnalysisMethod {

	/**
	 * Contructor del método del codo.
	 * 
	 * @param algorithm algoritmo de clustering
	 */
	public ElbowMethod(Algorithm algorithm) {
		super(algorithm, Comparator.<Double>naturalOrder());
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getYLabel() {
		return "clustering.analyze.elbow.yLabel";
	}

}
