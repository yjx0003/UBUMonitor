package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

public class MultiKMeansPlusPlus extends KMeansPlusPlus {

	public static final String NAME = "MultiKMeansPlusPlus";

	public MultiKMeansPlusPlus() {
		super();
		setName(NAME);
		addParameter(ClusteringParameter.NUM_TRIALS, 3);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		KMeansPlusPlusClusterer<T> clusterer = (KMeansPlusPlusClusterer<T>) super.getClusterer();
		int trials = getParameters().getValue(ClusteringParameter.NUM_TRIALS);

		if (!ClusteringParameter.NUM_TRIALS.isValid(trials))
			throw new IllegalParamenterException(ClusteringParameter.NUM_TRIALS, trials);

		return new MultiKMeansPlusPlusClusterer<>(clusterer, trials);
	}
}
