package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.UserData;

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
