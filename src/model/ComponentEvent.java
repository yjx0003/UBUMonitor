package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase para emparejar componente y evento.
 * 
 * @author Yi Peng Ji
 *
 */
public class ComponentEvent {

	private Component component;
	private Event eventName;

	private static Map<Component, Map<Event, ComponentEvent>> componentEventFactory = new HashMap<>();

	private ComponentEvent(Component component, Event eventName) {
		this.component = component;
		this.eventName = eventName;
	}

	/**
	 * Devuelve un objecto ComponentEvent a partir del Component y Event. Si ya hay
	 * almacenado el ComponentEvent, devuelve ese y si no crea uno nuevo y lo guarda.
	 * 
	 * @param component
	 *            componente
	 * @param eventName
	 *            event
	 * @return ComponentEvent asociado
	 */
	public static ComponentEvent get(String component, String eventName) {
		return get(Component.get(component), Event.get(eventName));
	}

	/**
	 * Devuelve un objecto ComponentEvent a partir del Component y Event. Si ya hay
	 * almacenado el ComponentEvent, devuelve ese y si no crea uno nuevo y lo guarda.
	 * 
	 * @param component
	 *            componente
	 * @param eventName
	 *            event
	 * @return ComponentEvent asociado
	 */
	public static ComponentEvent get(Component component, Event eventName) {
		Map<Event, ComponentEvent> events = componentEventFactory.computeIfAbsent(component,
				c -> new HashMap<Event, ComponentEvent>());
		return events.computeIfAbsent(eventName, m -> new ComponentEvent(component, eventName));
	}

	/**
	 * Devuelve el componente.
	 * @return componente
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Devuelve el evento.
	 * @return el evento
	 */
	public Event getEventName() {
		return eventName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentEvent other = (ComponentEvent) obj;
		if (component != other.component)
			return false;
		if (eventName != other.eventName)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return component + " - " + eventName;
	}

}
