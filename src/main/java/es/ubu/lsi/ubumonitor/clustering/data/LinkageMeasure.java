package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.function.BiFunction;

import es.ubu.lsi.ubumonitor.controllers.I18n;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.UPGMALinkage;
import smile.clustering.linkage.UPGMCLinkage;
import smile.clustering.linkage.WardLinkage;
import smile.math.distance.Distance;

public enum LinkageMeasure {

	COMPLETE(CompleteLinkage::of),
	SINGLE(SingleLinkage::of),
	AVERAGE(UPGMALinkage::of),
	CENTROID(UPGMCLinkage::of),
	WARD(WardLinkage::of);

	private BiFunction<double[][], Distance<double[]>, Linkage> function;

	private LinkageMeasure(BiFunction<double[][], Distance<double[]>, Linkage> function) {
		this.function = function;
	}

	public Linkage of(double[][] data, Distance<double[]> distance) {
		return function.apply(data, distance);
	}
	
	@Override
	public String toString() {
		return I18n.get("clustering.linkage." + super.toString());
	}

}
