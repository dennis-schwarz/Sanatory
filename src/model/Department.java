package model;

public class Department {
	private int ID;
	private double xCoordinate;
	private double yCoordinate;
	private double zCoordinate;
	private double weightingFactor;
	private boolean isDepartment;

	public Department(int ID, double xCoordinate, double yCoordinate,
			double zCoordinate, double weightingFactor) {
		this.ID = ID;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.zCoordinate = zCoordinate;
		this.weightingFactor = weightingFactor;
	}

	public int getID() {
		return ID;
	}

	public double getWeightingFactor() {
		return weightingFactor;
	}
}
