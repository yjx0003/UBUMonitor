package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

public abstract class Algorithm {

	private String name;
	private String library;
	private AlgorithmParameters parameters = new AlgorithmParameters();;

	protected Algorithm(String name, String library) {
		setName(name);
		this.library = library;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getLibrary() {
		return library;
	}

	public AlgorithmParameters getParameters() {
		return parameters;
	}

	protected void addParameter(ClusteringParameter parameter, Object value) {
		parameters.addParameter(parameter, value);
	}

	protected void checkParameter(ClusteringParameter parameter, Number value) {
		if (!parameter.isValid(value))
			throw new IllegalParamenterException(parameter, value);
	}

	public abstract Clusterer<UserData> getClusterer();

	@Override
	public String toString() {
		return name + " (" + library + ")";
	}
}
