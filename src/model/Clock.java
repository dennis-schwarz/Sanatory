package model;

import java.util.Date;

/*
 * Braucht es die Clock nicht nur bei der Visualisierung, sprich im POVRAY?
 * Wir habe alle Zeiten im Date-Format hier. Wenn wir bei der Animation eine Clock haben, sollte das doch reichen, oder?
 */
@SuppressWarnings("unused")
public class Clock {
	private static Clock uniqueInstance;
	private Date firstEntry;
	private Date lastExit;
	private Date currentTime;

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
