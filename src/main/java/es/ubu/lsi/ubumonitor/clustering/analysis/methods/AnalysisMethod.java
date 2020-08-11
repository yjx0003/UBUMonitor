package es.ubu.lsi.ubumonitor.clustering.analysis.methods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

/**
 * Clase basa de un método de análisis.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class AnalysisMethod {

	private static final int TRIALS = 30;

	private Algorithm algorithm;
	private Comparator<Double> comparator;

	/**
	 * Constructor de un método de análisis.
	 * 
	 * @param algorithm  algoritmo de clustering
	 * @param comparator comparador del resultado del analisis
	 */
	protected AnalysisMethod(Algorithm algorithm, Comparator<Double> comparator) {
		this.algorithm = algorithm;
		this.comparator = comparator;
	}

	/**
	 * Realiza el analisis con un número de agrupaciones desde start (incluido)
	 * hasta end (incluido).
	 * 
	 * @param start      comienzo del intervalo
	 * @param end        fin del intervalo
	 * @param users      lista de usuarios matriculados
	 * @param collectors lista de colectores de datos
	 * @return lista con los resultados de los análisis
	 */
	public List<Double> analyze(int start, int end, List<EnrolledUser> users, List<DataCollector> collectors) {
		List<Double> result = new ArrayList<>();
		int initial = algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		for (int i = start; i <= end; i++) {
			double best = Double.NaN;
			for (int j = 0; j < TRIALS; j++) {
				algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, i);
				AlgorithmExecuter executer = new AlgorithmExecuter(algorithm, users, collectors);
				List<ClusterWrapper> clusters = executer.execute(0, true);
				double value = calculate(clusters);
				if (Double.isNaN(best) || comparator.compare(best, value) > 0)
					best = value;
			}
			result.add(best);
		}
		algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, initial);
		return result;
	}

	/**
	 * Metodo que realiza el analisis de un resualtado de clustering.
	 * 
	 * @param clusters lista de agrupaciones
	 * @return resulatado del análisis
	 */
	protected abstract double calculate(List<ClusterWrapper> clusters);

	/**
	 * Devuelve la métrica empleada en el analisis.
	 * 
	 * @return nombre de la métrica
	 */
	public abstract String getYLabel();

	/**
	 * Devuelve la medida de distancia.
	 * 
	 * @return medida de distancia
	 */
	protected Distance getDistance() {
		Distance distance = algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);
		return distance != null ? distance : Distance.EUCLIDEAN_DISTANCE;
	}

}
