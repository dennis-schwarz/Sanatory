package model;

import java.util.Date;

public class Clock {
	private static Clock uniqueInstance;
	private Date firstEntry;
	private Date lastExit;
	private Date currentTime;

	private Clock() {

	}

	public Clock getUniqueInstance() {
		if (Clock.uniqueInstance == null) {
			Clock.uniqueInstance = new Clock();
		}

		return Clock.uniqueInstance;
	}

	public void setFirstEntry(Date firstEntry) {
		this.firstEntry = firstEntry;
	}

	public void setLastExit(Date lastExit) {
		this.lastExit = lastExit;
	}

}
