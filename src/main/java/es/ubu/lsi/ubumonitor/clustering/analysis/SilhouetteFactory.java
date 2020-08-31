package es.ubu.lsi.ubumonitor.clustering.analysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.SilhouetteMethod;
import es.ubu.lsi.ubumonitor.util.I18n;

public class SilhouetteFactory extends AnalysisFactory {

	@Override
	public AnalysisMethod createAnalysis(Algorithm algorithm) {
		return new SilhouetteMethod(algorithm);
	}

	@Override
	public String toString() {
		return I18n.get("clustering.analyze.silhouette");
	}

}
