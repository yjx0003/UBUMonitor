package controllers.ubulogs.logcreator;

import java.util.HashMap;
import java.util.Map;

public class ComponentEvent {
	


	private Component component;
	private Event eventName;
	
	private static Map<Component,Map<Event,ComponentEvent>> componentEventInstances=new HashMap<>();
	
	
	private ComponentEvent(Component component,Event eventName) {
		this.component=component;
		this.eventName=eventName;
	}
	
	public static ComponentEvent getInstance(String component,String eventName) {
		return getInstance(Component.get(component), Event.get(eventName));
	}
	
	public static ComponentEvent getInstance(Component component,Event eventName) {
		Map<Event,ComponentEvent> events= componentEventInstances.computeIfAbsent(component, c->new HashMap<Event,ComponentEvent>());
		return events.computeIfAbsent(eventName, m-> new ComponentEvent(component,eventName));
	}

	public Component getComponent() {
		return component;
	}

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
		return component.toString()+" - "+ eventName.toString();
	}

	
	
}
