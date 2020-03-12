package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;

public class MultiKMeansPlusPlus extends KMeansPlusPlus {

	public static final String NAME = "MultiKMeansPlusPlus";

	public MultiKMeansPlusPlus() {
		super();
		setName(NAME);
		addParameter("clustering.numTrials", 3);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		KMeansPlusPlusClusterer<T> clusterer = (KMeansPlusPlusClusterer<T>) super.getClusterer();
		int trials = getParameters().getValue("clustering.numTrials");
		return new MultiKMeansPlusPlusClusterer<>(clusterer, trials);
	}
}
