package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.AbstractList;

import org.apache.commons.math3.ml.clustering.Cluster;

public class ClusterWrapper extends AbstractList<UserData> {

	private int id;
	private String name;
	private Cluster<UserData> cluster;

	public ClusterWrapper(int id, Cluster<UserData> cluster) {
		this.id = id;
		this.name = String.valueOf(id);
		this.cluster = cluster;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cluster
	 */
	public Cluster<UserData> getCluster() {
		return cluster;
	}

	@Override
	public UserData get(int index) {
		return cluster.getPoints().get(index);
	}

	@Override
	public int size() {
		return cluster.getPoints().size();
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ClusterWrapper))
			return false;
		ClusterWrapper other = (ClusterWrapper) obj;
		return id == other.id;
	}

}
