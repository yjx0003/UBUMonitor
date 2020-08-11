package es.ubu.lsi.ubumonitor.clustering.analysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.ElbowMethod;
import es.ubu.lsi.ubumonitor.controllers.I18n;

public class ElbowFactory extends AnalysisFactory {

	@Override
	public AnalysisMethod createAnalysis(Algorithm algorithm) {
		return new ElbowMethod(algorithm);
	}
	
	@Override
	public String toString() {
		return I18n.get("clustering.analyze.elbow");
	}

}
