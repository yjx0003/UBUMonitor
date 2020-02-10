package clustering.controller.collector;

import java.util.List;

import clustering.data.UserData;

public abstract class DataCollector {

	public abstract void collect(List<UserData> users);

}
