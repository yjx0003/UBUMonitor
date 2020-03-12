package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterable;

import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class UserData implements Clusterable {

	private EnrolledUser user;
	private List<Datum> data;
	private List<Double> normalizedData;
	private int cluster;

	public UserData(EnrolledUser user) {
		this.user = user;
		data = new ArrayList<>();
		normalizedData = new ArrayList<>();
	}

	public EnrolledUser getEnrolledUser() {
		return user;
	}

	public void addNormalizedDatum(double datum) {
		normalizedData.add(Double.isNaN(datum) ? 0 : datum);
	}

	public void addDatum(Datum datum) {
		data.add(datum);
	}

	public void setData(double[] data) {
		this.normalizedData = Arrays.stream(data).boxed().collect(Collectors.toList());
	}

	@Override
	public double[] getPoint() {
		return normalizedData.stream().mapToDouble(Double::doubleValue).toArray();
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public int getCluster() {
		return cluster;
	}

	public List<Datum> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "UserData [user=" + user + ", data=" + data + ", cluster=" + cluster + "]";
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
