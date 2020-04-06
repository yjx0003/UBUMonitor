package es.ubu.lsi.ubumonitor.clustering.analysis;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.SilhouetteMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class SilhouetteFactory extends AnalysisFactory {

	@Override
	public AnalysisMethod createAnalysis(Algorithm algorithm, List<EnrolledUser> users,
			List<DataCollector> collectors) {
		return new SilhouetteMethod(algorithm, users, collectors);
	}
	
	@Override
	public String toString() {
		return I18n.get("clustering.silhourtte");
	}

}
