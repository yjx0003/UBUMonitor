package es.ubu.lsi.ubumonitor.clustering.analysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;

public abstract class AnalysisFactory {

	public abstract AnalysisMethod createAnalysis(Algorithm algorithm);

}
