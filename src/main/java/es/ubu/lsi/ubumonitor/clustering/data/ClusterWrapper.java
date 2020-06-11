package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.AbstractList;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;

/**
 * Clase que envuelve las agrupaciones.
 * 
 * @author Xing Long Ji
 *
 */
public class ClusterWrapper extends AbstractList<UserData> {

	private int id;
	private String name;
	private Cluster<UserData> cluster;

	/**
	 * Constructor.
	 * 
	 * @param id      identificador de la agrupación
	 * @param cluster agrupacion de usuarios
	 */
	public ClusterWrapper(int id, Cluster<UserData> cluster) {
		this.id = id;
		this.name = String.valueOf(id);
		this.cluster = cluster;
	}

	/**
	 * Devuelve el centro de la agrupación o null si no tiene centro.
	 * 
	 * @return las coordenadas del centro
	 */
	public double[] getCenter() {
		if (cluster instanceof CentroidCluster) {
			CentroidCluster<?> centroidCluster = (CentroidCluster<?>) cluster;
			return centroidCluster.getCenter().getPoint();
		}
		return null;
	}

	/**
	 * Devuelve el identificador.
	 * 
	 * @return el id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve el nombre de la agrupación.
	 * 
	 * @return el nombre
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el nombre de la agrupación.
	 * 
	 * @param name el nombre a establecer
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devulve un elemento de la agrupación. {@inheritDoc}
	 */
	@Override
	public UserData get(int index) {
		return cluster.getPoints().get(index);
	}

	/**
	 * Devuelve el número de elementos en la agrupación. {@inheritDoc}
	 */
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
