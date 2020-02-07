package clustering.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.ml.distance.CanberraDistance;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

public class Algorithms {

	public static final List<DistanceMeasure> DISTANCES_LIST = Collections
			.unmodifiableList(Arrays.asList(new CanberraDistance(), new ChebyshevDistance(), new EarthMoversDistance(),
					new EuclideanDistance(), new ManhattanDistance()));

	private static List<Algorithm> algorithmList = new ArrayList<>();

	static {
		algorithmList.add(new KMeansPlusPlus());
		algorithmList.add(new FuzzyKMeans());
		algorithmList.add(new DBSCAN());
		algorithmList.add(new MultiKMeansPlusPlus());
	}

	public static Collection<Algorithm> getAlgorithms() {
		return algorithmList;
	}

	private Algorithms() {
	}
}
