package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class SilhouetteMethod extends AnalysisMethod {

	public SilhouetteMethod(Algorithm algorithm, List<EnrolledUser> users, List<DataCollector> collectors) {
		super(algorithm, users, collectors);
	}

	@Override
	protected double calculate(List<ClusterWrapper> clusters) {
		Map<UserData, Double> silhouette = AlgorithmExecuter.silhouette(clusters, getDistance());
		OptionalDouble average = silhouette.values().stream().mapToDouble(Double::doubleValue).average();
		return average.getAsDouble();
	}

}
