package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public abstract class AnalysisMethod {

	private Algorithm algorithm;

	protected AnalysisMethod(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public List<Double> analyze(int start, int end, List<EnrolledUser> users, List<DataCollector> collectors) {
		List<Double> result = new ArrayList<>();
		int initial = algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		for (int i = start; i <= end; i++) {
			algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, i);
			AlgorithmExecuter executer = new AlgorithmExecuter(algorithm.getClusterer(), users, collectors);
			List<ClusterWrapper> clusters = executer.execute(0);
			double value = calculate(clusters);
			result.add(value);
		}
		algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, initial);
		return result;
	}

	protected abstract double calculate(List<ClusterWrapper> clusters);

	protected Distance getDistance() {
		return algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);
	}

}
