package es.ubu.lsi.ubumonitor.clustering.analysis;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public abstract class AnalysisFactory {

	public abstract AnalysisMethod createAnalysis(Algorithm algorithm, List<EnrolledUser> users,
			List<DataCollector> collectors);

}
