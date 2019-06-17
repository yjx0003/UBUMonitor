package model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Clase para emparejar componente y evento.
 * 
 * @author Yi Peng Ji
 *
 */
public class ComponentEvent {

	private Component component;
	private Event eventName;

	private static Map<Component, Map<Event, ComponentEvent>> componentEventFactory = new EnumMap<>(Component.class);

	private ComponentEvent(Component component, Event eventName) {
		this.component = component;
		this.eventName = eventName;
	}

	/**
	 * Devuelve un objecto ComponentEvent a partir del Component y Event. Si ya hay
	 * almacenado el ComponentEvent, devuelve ese y si no crea uno nuevo y lo
	 * guarda.
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
	 * almacenado el ComponentEvent, devuelve ese y si no crea uno nuevo y lo
	 * guarda.
	 * 
	 * @param component
	 *            componente
	 * @param eventName
	 *            event
	 * @return ComponentEvent asociado
	 */
	public static ComponentEvent get(Component component, Event eventName) {
		Map<Event, ComponentEvent> events = componentEventFactory.computeIfAbsent(component,
				c -> new EnumMap<>(Event.class));
		return events.computeIfAbsent(eventName, m -> new ComponentEvent(component, eventName));
	}

	/**
	 * Devuelve el componente.
	 * 
	 * @return componente
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Devuelve el evento.
	 * 
	 * @return el evento
	 */
	public Event getEventName() {
		return eventName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(component, eventName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof ComponentEvent))
			return false;

		ComponentEvent other = (ComponentEvent) obj;
		return component == other.component && eventName == other.eventName;

	}

	@Override
	public String toString() {
		return component + " - " + eventName;
	}

}
