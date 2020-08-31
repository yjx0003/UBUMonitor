package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

/**
 * Clase base de los algoritmos de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class Algorithm {

	private String name;
	private String library;
	private AlgorithmParameters parameters = new AlgorithmParameters();

	/**
	 * Constructor de un algoritmo.
	 * 
	 * @param name    nombre del algoritmo
	 * @param library biblioteca a la que pertecence
	 */
	protected Algorithm(String name, String library) {
		setName(name);
		setLibrary(library);
	}

	/**
	 * Añade un nuevo parametros al algortimo.
	 * 
	 * @param parameter parametro
	 * @param value     valor por defecto
	 */
	protected void addParameter(ClusteringParameter parameter, Object value) {
		parameters.addParameter(parameter, value);
	}

	/**
	 * Comprueba si el valor es válido para el parametro.
	 * 
	 * @param parameter parametro
	 * @param value     valor a comprobar
	 * @throws IllegalParamenterException si el valor no es válido
	 */
	protected void checkParameter(ClusteringParameter parameter, Number value) {
		if (!parameter.isValid(value))
			throw new IllegalParamenterException(parameter, value);
	}

	/**
	 * Devulve el clusterer del algoritmo.
	 * 
	 * @return clusterer del algoritmo
	 */
	public abstract Clusterer<UserData> getClusterer();

	/**
	 * Establece el nombre del algoritmo.
	 * 
	 * @param name nombre del algoritmo
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Establece el nombre de la biblioteca del algoritmo.
	 * 
	 * @param library nombre de la libreria
	 */
	public void setLibrary(String library) {
		this.library = library;
	}

	/**
	 * Devuleve el nombre del algoritmo.
	 * 
	 * @return nombre del algoritmo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuleve el nombre de la biblioteca del algoritmo.
	 * 
	 * @return nombre de la biblioteca
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * Devulve los parametros del algoritmo.
	 * 
	 * @return parametros del algoritmo
	 */
	public AlgorithmParameters getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return name + " (" + library + ")";
	}
}
