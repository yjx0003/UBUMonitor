package controllers.UBULogs.logCreator;

public class ComponentEventKey {
	


	private Component component;
	private EventName eventName;
	
	public ComponentEventKey(Component component,EventName eventName) {
		this.component=component;
		this.eventName=eventName;
	}
	
	public ComponentEventKey(String component,String eventName) {
		this(Component.get(component),EventName.get(eventName));
	}

	public Component getComponent() {
		return component;
	}

	public EventName getEventName() {
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
		ComponentEventKey other = (ComponentEventKey) obj;
		if (component != other.component)
			return false;
		if (eventName != other.eventName)
			return false;
		return true;
	}

	
	
}
