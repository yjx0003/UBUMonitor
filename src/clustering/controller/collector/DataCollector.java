package clustering.controller.collector;

import java.util.List;

import clustering.data.UserData;

public abstract class DataCollector {

	private String type;

	protected DataCollector(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public abstract void collect(List<UserData> users);

}
