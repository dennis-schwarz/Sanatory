package model;

import java.util.ArrayList;
import java.util.Date;

public class Patient {
	private int ID;
	private Date entry;
	private ArrayList<Movement> movements;
	private Date exit;
	private double currentX;
	private double currentY;
	private double currentZ;

	public Patient(int iD, Date entry, ArrayList<Movement> movements, Date exit) {
		super();
		ID = iD;
		this.entry = entry;
		this.movements = movements;
		this.exit = exit;
		this.currentX = 0;
		this.currentY = 0;
		this.currentZ = 0;
	}

}
