package es.ubu.lsi.ubumonitor.util;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.datasets.DataSet;

public interface LogAction<T> {

	public  <E> T action(List<E> logType, DataSet<E> dataSet);

}
