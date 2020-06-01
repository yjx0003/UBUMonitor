package es.ubu.lsi.ubumonitor.clustering.data;

import es.ubu.lsi.ubumonitor.controllers.I18n;

public enum ClusteringParameter {

	NUM_CLUSTER("numberOfClusters", 2),
	MAX_ITERATIONS("maxIterations", 1),
	DISTANCE_TYPE("distance", -1),
	FUZZINESS("fuzziness", 1.1),
	EPS("eps", 0), MIN_POINTS("minPts", 0),
	NUM_TRIALS("numTrials", 1),
	TOLERANCE("tol", 0),
	MAX_NUM_CLUSTER("maxNumberOfClusters", 2),
	ANNELING_CONTROL("anneling_control", 0, 1),
	SPLIT_TOLERANCE("splitTol", 0),
	SMOOTH("smooth", 0);

	private String name;
	private double min;
	private double max;

	private ClusteringParameter(String name, double min) {
		this(name, min, Double.POSITIVE_INFINITY);
	}

	private ClusteringParameter(String name, double min, double max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}

	public String getName() {
		return I18n.get("clustering." + name);
	}

	public String getDescription() {
		return I18n.get("clustering." + name + ".tooltip");
	}

	public boolean isValid(Number value) {
		return value.doubleValue() >= min && value.doubleValue() <= max;
	}

	public double getMin() {
		return min;
	}

	@Override
	public String toString() {
		return getName();
	}

}
