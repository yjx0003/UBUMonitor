package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Algorithms {

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
		throw new UnsupportedOperationException();
	}
}
