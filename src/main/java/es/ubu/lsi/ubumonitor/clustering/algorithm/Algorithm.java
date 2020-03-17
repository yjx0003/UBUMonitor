package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;

public abstract class Algorithm {

	private String name;
	private AlgorithmParameters parameters;

	protected Algorithm(String name) {
		this.name = name;
		parameters = new AlgorithmParameters();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public AlgorithmParameters getParameters() {
		return parameters;
	}

	protected void addParameter(ClusteringParameter parameter, Object value) {
		parameters.addParameter(parameter, value);
	}

	public abstract <T extends Clusterable> Clusterer<T> getClusterer();

	@Override
	public String toString() {
		return name;
	}
}
