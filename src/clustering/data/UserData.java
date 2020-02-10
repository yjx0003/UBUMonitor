package clustering.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.Clusterable;

import model.EnrolledUser;

public class UserData implements Clusterable {

	private EnrolledUser user;
	private Map<String, Double> data;
	private int cluster;

	public UserData(EnrolledUser user) {
		this.user = user;
		data = new LinkedHashMap<>();
	}

	public EnrolledUser getEnrolledUser() {
		return user;
	}

	public void addDatum(String name, double datum) {
		data.put(name, Double.isNaN(datum) ? 0 : datum);
	}

	@Override
	public double[] getPoint() {
		return data.values().stream().mapToDouble(Double::doubleValue).toArray();
	}

	public void setCluster(int n) {
		cluster = n;
	}

	public int getCluster() {
		return cluster;
	}

	public double getValue(String name) {
		return data.get(name);
	}

	@Override
	public String toString() {
		return "UserData [user=" + user + ", data=" + data + ", cluster=" + cluster + "]";
	}

	public Set<String> getKeys() {
		return data.keySet();
	}
}
