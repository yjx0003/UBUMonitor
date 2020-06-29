package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterable;

import es.ubu.lsi.ubumonitor.model.EnrolledUser;

/**
 * Clase que contiene los datos de un usuario.
 * 
 * @author Xing Long Ji
 *
 */
public class UserData implements Clusterable {

	private EnrolledUser user;
	private List<Datum> data;
	private List<Double> normalizedData;
	private ClusterWrapper cluster;

	/**
	 * Constructor.
	 * 
	 * @param user usuario
	 */
	public UserData(EnrolledUser user) {
		this.user = user;
		data = new ArrayList<>();
		normalizedData = new ArrayList<>();
	}

	/**
	 * Constructor para clonar.
	 * 
	 * @param userData UserData a clonar
	 */
	public UserData(UserData userData) {
		this.user = userData.user;
		this.data = userData.data;
		this.normalizedData = userData.normalizedData;
	}

	/**
	 * Añade el dato normalizado.
	 * 
	 * @param datum dato
	 */
	public void addNormalizedDatum(double datum) {
		normalizedData.add(Double.isNaN(datum) ? 0 : datum);
	}

	/**
	 * Añade un dato.
	 * 
	 * @param datum dato
	 */
	public void addDatum(Datum datum) {
		data.add(datum);
	}

	/**
	 * Elimina un dato.
	 * 
	 * @param index indice
	 */
	public void removeDatum(int index) {
		data.remove(index);
		normalizedData.remove(index);
	}

	/**
	 * Establece los datos.
	 * 
	 * @param data array de valores
	 */
	public void setData(double[] data) {
		this.normalizedData = Arrays.stream(data).boxed().collect(Collectors.toList());
	}

	/**
	 * Establece la agrupación a la que pertenece este usuraio.
	 * 
	 * @param cluster agrupación
	 */
	public void setCluster(ClusterWrapper cluster) {
		this.cluster = cluster;
	}

	/**
	 * Devuelve los datos normalizados.
	 */
	@Override
	public double[] getPoint() {
		return normalizedData.stream().mapToDouble(Double::doubleValue).toArray();
	}

	/**
	 * Devuelve el usuario.
	 * 
	 * @return el usuario
	 */
	public EnrolledUser getEnrolledUser() {
		return user;
	}

	/**
	 * Devuelve la agrupación a la que pertenece.
	 * 
	 * @return la agrupación
	 */
	public ClusterWrapper getCluster() {
		return cluster;
	}

	/**
	 * Devuelve la lista de datos.
	 * 
	 * @return lista de datos
	 */
	public List<Datum> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "UserData [user=" + user + ", cluster=" + cluster.getName() + "]";
	}

	@Override
	public int hashCode() {
		return user.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof UserData))
			return false;
		UserData other = (UserData) obj;
		return user.equals(other.user);
	}

}
