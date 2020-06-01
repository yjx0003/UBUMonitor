package es.ubu.lsi.ubumonitor.clustering.algorithm.apache;

import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

public class MultiKMeansPlusPlus extends KMeansPlusPlus {

	private static final String NAME = "MultiKMeansPlusPlus";

	public MultiKMeansPlusPlus() {
		super();
		setName(NAME);
		addParameter(ClusteringParameter.NUM_TRIALS, 3);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		KMeansPlusPlusClusterer<UserData> clusterer = (KMeansPlusPlusClusterer<UserData>) super.getClusterer();
		int trials = getParameters().getValue(ClusteringParameter.NUM_TRIALS);

		checkParameter(ClusteringParameter.NUM_TRIALS, trials);

		return new MultiKMeansPlusPlusClusterer<>(clusterer, trials);
	}
}
