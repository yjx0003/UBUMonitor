package model;

import java.time.Instant;

public class ActivityCompletion {
	private State state;
	

	private Instant timecompleted;
	private Tracking tracking;
	private EnrolledUser overrideby;
	private boolean valueused; 
	
	
	public ActivityCompletion(State state, Instant timecompleted, Tracking tracking, EnrolledUser overrideby,
			boolean valueused) {
		this.state = state;
		this.timecompleted = timecompleted;
		this.tracking = tracking;
		this.overrideby = overrideby;
		this.valueused = valueused;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Instant getTimecompleted() {
		return timecompleted;
	}

	public void setTimecompleted(Instant timecompleted) {
		this.timecompleted = timecompleted;
	}

	public Tracking getTracking() {
		return tracking;
	}

	public void setTracking(Tracking tracking) {
		this.tracking = tracking;
	}

	public EnrolledUser getOverrideby() {
		return overrideby;
	}

	public void setOverrideby(EnrolledUser overrideby) {
		this.overrideby = overrideby;
	}

	
	
	public boolean isValueused() {
		return valueused;
	}

	public void setValueused(boolean valueused) {
		this.valueused = valueused;
	}



	public enum State {
		INCOMPLETE, COMPLETE, COMPLETE_PASS, COMPLETE_FAIL;

		private final static State[] ALL_STATUS = State.values();

		public static State getByIndex(int index) {
			return ALL_STATUS[index];
		}
	}

	public enum Tracking {
		NONE, MANUAL, AUTOMATIC;
		
		private final static Tracking[] ALL_TRACKINGS = Tracking.values();
		public static Tracking getByIndex(int index) {
			return ALL_TRACKINGS[index];
		}
	}
}
