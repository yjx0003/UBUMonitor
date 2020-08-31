package es.ubu.lsi.ubumonitor.clustering.data;

import org.apache.commons.math3.ml.distance.CanberraDistance;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

import es.ubu.lsi.ubumonitor.util.I18n;

/**
 * Enumeración de la medidas de distancia.
 * 
 * @author Xing Long Ji
 *
 */
public enum Distance {

	MANHATTAN_DISTANCE(new ManhattanDistance()),
	EUCLIDEAN_DISTANCE(new EuclideanDistance()),
	CANBERRA_DISTANCE(new CanberraDistance()),
	CHEBYSHEV_DISTANCE(new ChebyshevDistance()),
	EARTHMOVERS_DISTANCE(new EarthMoversDistance());

	private DistanceMeasure instance;

	private Distance(DistanceMeasure instance) {
		this.instance = instance;
	}

	/**
	 * Devulve la instancia.
	 * 
	 * @return instacia
	 */
	public DistanceMeasure getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return I18n.get("clustering.distance." + super.toString().toLowerCase());
	}

}
