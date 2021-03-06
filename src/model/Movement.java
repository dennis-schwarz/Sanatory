package model;

import java.util.Date;

public class Movement {
	private Department from;
	private Department to;
	private Date entry;
	private int type;

	public Movement(Department from, Department to, Date entry, int type) {
		this.from = from;
		this.to = to;
		this.entry = entry;
		this.type = type;
	}

	public Department whereAmI() {
		return from;
	}

	public Department whereDoIGo() {
		return to;
	}

	public Date whenDoIStart() {
		return entry;
	}

	public int howDoIMove() {
		return type;
	}

}
