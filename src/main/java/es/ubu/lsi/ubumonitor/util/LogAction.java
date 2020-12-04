package es.ubu.lsi.ubumonitor.util;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.FirstGroupBy;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;

public interface LogAction<R> {

	public <E extends Serializable, T extends Serializable> R action(List<E> logType, DataSet<E> dataSet,
			Function<GroupByAbstract<?>, FirstGroupBy<E, T>> function);

}
