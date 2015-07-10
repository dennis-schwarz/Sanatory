package application;

@SuppressWarnings("unused")
public class ApplicationContext {
	final private String version = "1.0";
	private String departmentsFile = "data/departments.txt";
	private String movementsFile = "data/movements.txt";
	private String outputFile = "data/patients.inc";
	private String iniFile = "data/patients.ini";
	private String clockFile = "data/clock.inc";
	// which day should be shown (i.e. "20120320000000")
	private String outputDate = "20120712000000"; // start date - 00:00:00
	private int howManyDays = 2; // x days will be shown (until 23:59:59)

	public ApplicationContext() {

	}

	public String getDepartmentsFile() {
		return departmentsFile;
	}

	public String getMovementsFile() {
		return movementsFile;
	}

	public String getIniFile() {
		return iniFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getClockFile() {
		return clockFile;
	}

	public String getOutputDate() {
		return outputDate;
	}

	public int getHowManyDays() {
		return howManyDays;
	}

}
