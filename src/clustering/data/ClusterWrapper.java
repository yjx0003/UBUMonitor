package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.AbstractList;

import org.apache.commons.math3.ml.clustering.Cluster;

public class ClusterWrapper extends AbstractList<UserData> {

	private String name;
	private Cluster<UserData> cluster;

	public ClusterWrapper(String name, Cluster<UserData> cluster) {
		this.name = name;
		this.cluster = cluster;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ClusterWrapper))
			return false;
		ClusterWrapper other = (ClusterWrapper) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
