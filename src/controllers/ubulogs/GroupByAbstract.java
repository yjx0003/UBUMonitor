package controllers.ubulogs;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.LogLine;
import model.Section;

/**
 * Clase abstracta con metodos de creacion de agrupamiento de los logs.
 * 
 * @author Yi Peng Ji
 *
 * @param <T>
 *            tipo de agrupamiento
 */
public abstract class GroupByAbstract<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	FirstGroupBy<Component, T> components;
	FirstGroupBy<ComponentEvent, T> componentsEvents;
	FirstGroupBy<Section, T> sections;
	FirstGroupBy<CourseModule, T> courseModules;

	/**
	 * Constructor para agrupar la lineas de log en funcion de los usuarios.
	 * 
	 * @param logLines
	 *            las lineas de log
	 */
	public GroupByAbstract(List<LogLine> logLines) {
		
		
		
		components = new FirstGroupBy<>(this, logLines, LogLine::hasUser, LogLine::getComponent,
				getGroupByFunction());
		componentsEvents = new FirstGroupBy<>(this,logLines, LogLine::hasUser, LogLine::getComponentEvent,
				getGroupByFunction());
		sections = new FirstGroupBy<>(this,logLines, l -> l.hasUser() && l.hasSection(),
				LogLine::getSection, getGroupByFunction());
		courseModules = new FirstGroupBy<>(this,logLines, l -> l.hasUser() && l.hasCourseModule(), LogLine::getCourseModule,
				getGroupByFunction());
	}
	

	public FirstGroupBy<Component, T> getComponents() {
		return components;
	}


	public FirstGroupBy<ComponentEvent, T> getComponentsEvents() {
		return componentsEvents;
	}


	public FirstGroupBy<Section, T> getSections() {
		return sections;
	}


	public FirstGroupBy<CourseModule, T> getCourseModules() {
		return courseModules;
	}


	/**
	 * Devuelve los rangos en formato string
	 * 
	 * @param start
	 *            fecha de inico
	 * @param end
	 *            fecha de fin
	 * @return rangos de un tipo de fecha en formato string
	 */
	public List<String> getRangeString(LocalDate start, LocalDate end) {
		return getRangeString(getRange(start, end));
	}
	
	/**
	 * Devuelve los rangos en formato string
	 * 
	 * @param rangeList
	 *            lista de rangos
	 * @return rangos de un tipo de fecha en formato string
	 */
	public List<String> getRangeString(List<T> rangeList) {
		return rangeList.stream()
				.map(getStringFormatFunction())
				.collect(Collectors.toList());
	}


	/**
	 * Devuelve el rango entre dos local date
	 * 
	 * @param start
	 *            fecha de inicio
	 * @param end
	 *            fecha de fin
	 * @return rango del tipo de tiempo entre dos fechas
	 */
	public abstract List<T> getRange(LocalDate start, LocalDate end);

	/**
	 * La funcion usada para agrupar el tipo de tiempo
	 * 
	 * @return devuelve la funcion que se usa para agrupar
	 */
	public abstract Function<LogLine, T> getGroupByFunction();

	/**
	 * Devuelve la funcion de cómo se formatea el tipo T
	 * 
	 * @return la función de formateo
	 */
	public abstract Function<T, String> getStringFormatFunction();

	/**
	 * Devuelve que forma de medir el tiempo es.
	 * 
	 * @return forma de medir
	 */
	public abstract TypeTimes getTypeTime();

	/**
	 * Si el tipo de agrupación usa el date picker
	 * 
	 * @return true si lo usa, false en caso contrario
	 */
	public abstract boolean useDatePicker();

}
