package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class UnitTest {

	@Test
	public void rankingTest() {
		Map<Integer, DescriptiveStatistics> map = new HashMap<>();
		DescriptiveStatistics d1 = new DescriptiveStatistics();
		d1.addValue(100.0);
		DescriptiveStatistics d2 = new DescriptiveStatistics();
		d2.addValue(100.0);
		map.put(1, d1);
		map.put(2, d2);
		assertEquals(d1.getMean(), d2.getMean());
		Map<Integer, Integer> ranking = UtilMethods.ranking(map, DescriptiveStatistics::getMean);
		assertEquals(ranking.get(1), ranking.get(2));

	}

	@Test
	public void rankingTestStats() {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 2);
		map.put(4, 2);
		map.put(6, 2);
		map.put(5, 3);

		Map<Integer, Double> ranking = UtilMethods.rankingStatistics(map);
		System.out.println(ranking);

	}

	@Test
	public void rankColor() {
		int max = 27;
		int rankDivision = (int) Math.ceil(max / (double) 4);
		for (int i = 1; i <= max; ++i) {
			System.out.println((i) / rankDivision - 1);
		}
	}

	@Test
	public void rankColor1() {
		List<String> datasets = new ArrayList<String>();
		System.out.println(datasets.stream()
				.map(String::toString)
				.collect(Collectors.joining(",")));
	}
}
