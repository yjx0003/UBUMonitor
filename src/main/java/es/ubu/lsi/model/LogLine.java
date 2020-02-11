package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;

import org.threeten.extra.AmPm;
import org.threeten.extra.YearQuarter;
import org.threeten.extra.YearWeek;

/**
 * Las líneas de log que guardan los datos de varias columnas de los logs.
 * 
 * @author Yi Peng Ji
 *
 */
public class LogLine implements Serializable {

	private static final long serialVersionUID = 1L;

	private ZonedDateTime time;
	private Component component;
	private Event eventName;
	private Origin origin;
	private String ipAdress;

	private EnrolledUser user;
	private EnrolledUser affectedUser;
	private CourseModule courseModule;

	/**
	 * Devuelve el modulo del curso asociado al log, null si el log no tiene el
	 * modulo del curso.
	 * 
	 * @return el modulo del curso o null si el log no tiene
	 */
	public CourseModule getCourseModule() {
		return courseModule;
	}

	/**
	 * Asigna el modulo del curso a log y se añade el log al modulo del curso
	 * (bidireccional).
	 * 
	 * @param courseModule
	 *            modulo del curso
	 */
	public void setCourseModule(CourseModule courseModule) {
		this.courseModule = courseModule;
	}

	/**
	 * Devuelve true si existe modulo del curso asociado a este log
	 * 
	 * @return true si existe modulo del curso asociado a este log
	 */
	public boolean hasCourseModule() {
		return courseModule != null;
	}

	public boolean hasSection() {
		return hasCourseModule() && courseModule.getSection() != null;
	}
	
	public Section getSection() {
		return courseModule.getSection();
	}
	
	/**
	 * Devuelve la fecha del log.
	 * 
	 * @return la fecha del log
	 */
	public ZonedDateTime getTime() {
		return time;
	}

	/**
	 * Modifica la fecha del log.
	 * 
	 * @param time
	 *            nueva fecha
	 */
	public void setTime(ZonedDateTime time) {
		this.time = time;
	}

	/**
	 * Devuelve el componente.
	 * 
	 * @return el componente del log
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Modifica el componente.
	 * 
	 * @param component
	 *            componente
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * Devuelve el nombre del evento.
	 * 
	 * @return nombre del evento
	 */
	public Event getEventName() {
		return eventName;
	}

	/**
	 * Devuelve el par componente y evento.
	 * 
	 * @return componente y evento
	 */
	public ComponentEvent getComponentEvent() {
		return ComponentEvent.get(component, eventName);
	}

	/**
	 * Modifica el nombre del evento.
	 * 
	 * @param eventName
	 *            nombre del evento
	 */
	public void setEventName(Event eventName) {
		this.eventName = eventName;
	}

	/**
	 * Devuelve el origen del log.
	 * 
	 * @return origen del log
	 */
	public Origin getOrigin() {
		return origin;
	}

	/**
	 * Modifica el origen del log.
	 * 
	 * @param origin
	 *            origin
	 */
	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

	/**
	 * Devuelve la ip
	 * 
	 * @return la ip
	 */
	public String getIPAdress() {
		return ipAdress;
	}

	/**
	 * Modfica la ip del log.
	 * 
	 * @param iPAdress
	 *            direccion ip
	 */
	public void setIPAdress(String iPAdress) {
		ipAdress = iPAdress;
	}

	/**
	 * Devuelve el usuario que ha realizado la acción.
	 * 
	 * @return el usuario que ha realizado la acción
	 */
	public EnrolledUser getUser() {
		return user;
	}

	/**
	 * Modifica el usuario que ha realizado la acción. Bidireccional.
	 * 
	 * @param user
	 *            usuario
	 */
	public void setUser(EnrolledUser user) {
		this.user = user;
	}

	public boolean hasUser() {
		return user != null;
	}

	/**
	 * Devuelve el usuario afecto.
	 * 
	 * @return devuelve el usuario afectado
	 */
	public EnrolledUser getAffectedUser() {
		return affectedUser;
	}

	/**
	 * Modifica el usuario afectado. Bidireccional.
	 * 
	 * @param affectedUser
	 *            el nuevo usuario afecado
	 */
	public void setAffectedUser(EnrolledUser affectedUser) {
		this.affectedUser = affectedUser;
	}

	/**
	 * Devuelve si es AM o PM del log.
	 * 
	 * @return AM o PM del log
	 */
	public AmPm getAmPm() {
		return AmPm.from(time);
	}

	/**
	 * Devuelve la hora del log.
	 * 
	 * @return hora del log
	 */
	public int getHour() {
		return time.getHour();
	}

	/**
	 * Devuelve el dia de la semana del log.
	 * 
	 * @return dia de la semana
	 */
	public DayOfWeek getDayOfWeek() {
		return time.getDayOfWeek();
	}

	/**
	 * Devuelve el dia del mes del log.
	 * 
	 * @return dia del mes del log
	 */
	public int getDayOfMonth() {
		return time.getDayOfMonth();
	}

	/**
	 * Devuelve el dia del año.
	 * 
	 * @return el dia del año
	 */
	public int getDayOfYear() {
		return time.getDayOfYear();
	}

	/**
	 * Devuelve la fecha.
	 * 
	 * @return la fecha
	 */
	public LocalDate getLocalDate() {
		return time.toLocalDate();
	}

	/**
	 * Devuelve el mes.
	 * 
	 * @return el mes
	 */
	public Month getMonth() {
		return time.getMonth();
	}

	/**
	 * Devuelve mes y año.
	 * 
	 * @return mes y año
	 */
	public YearMonth getYearMonth() {
		return YearMonth.from(time);
	}

	/**
	 * Devuelve numero de semana del año y año.
	 * 
	 * @return numero de semana del año y año
	 */
	public YearWeek getYearWeek() {
		return YearWeek.from(time);
	}

	/**
	 * Devuelve el año.
	 * 
	 * @return el año
	 */
	public Year getYear() {
		return Year.from(time);
	}

	/**
	 * Devuelve el numero de trimestre del año y año.
	 * 
	 * @return el numero de trimestre del año y año
	 */
	public YearQuarter getYearQuarter() {
		return YearQuarter.from(time);
	}

	/**
	 * Devuelve el número de semana del año
	 * 
	 * @return el número de semana del log
	 */
	public int getWeekOfYear() {
		return time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}

	@Override
	public String toString() {
		return "LogLine [time=" + time + ", component=" + component + ", eventName=" + eventName + ", origin=" + origin
				+ ", IPAdress=" + ipAdress + ", user=" + user + ", affectedUser=" + affectedUser + ", courseModule="
				+ courseModule + "]";
	}

	



}
