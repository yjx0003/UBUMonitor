package es.ubu.lsi.ubumonitor.clustering.data;

import es.ubu.lsi.ubumonitor.controllers.I18n;

public enum ClusteringParameter {

	NUM_CLUSTER("numberOfClusters", 1),
	MAX_ITERATIONS("maxIterations", 1),
	DISTANCE_TYPE("distance", -1),
	FUZZINESS("fuzziness", 1.1),
	EPS("eps", 0),
	MIN_POINTS("minPts", 0),
	NUM_TRIALS("numTrials", 1);

	private String name;
	private double min;

	private ClusteringParameter(String name, double min) {
		this.name = name;
		this.min = min;
	}

	public String getName() {
		return I18n.get("clustering." + name);
	}

	public String getDescription() {
		return I18n.get("clustering." + name + ".tooltip");
	}

	public boolean isValid(Number value) {
		return value.doubleValue() >= min;
	}
	
	public double getMin() {
		return min;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
