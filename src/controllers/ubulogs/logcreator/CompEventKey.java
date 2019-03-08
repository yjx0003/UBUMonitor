package controllers.ubulogs.logcreator;

public class CompEventKey {
	


	private Component component;
	private Event eventName;
	
	public CompEventKey(Component component,Event eventName) {
		this.component=component;
		this.eventName=eventName;
	}
	
	public CompEventKey(String component,String eventName) {
		this(Component.get(component),Event.get(eventName));
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
		CompEventKey other = (CompEventKey) obj;
		if (component != other.component)
			return false;
		if (eventName != other.eventName)
			return false;
		return true;
	}

	
	
}
