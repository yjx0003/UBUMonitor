package clustering.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Clusterable;

import model.EnrolledUser;

public class UserData implements Clusterable {

	private EnrolledUser user;
	private List<Double> data;

	public UserData(EnrolledUser user) {
		this.user = user;
		data = new ArrayList<Double>();
	}
	
	public EnrolledUser getUser() {
		return user;
	}

	public void addDatum(double datum) {
		data.add(datum);
	}

	@Override
	public double[] getPoint() {
		double[] point = new double[data.size()];
		for (int i = 0; i < point.length; i++) {
			point[i] = data.get(i);
		}
		return point;
	}

	@Override
	public String toString() {
		return "UserData [user=" + user + ", data=" + data + "]";
	}
}
