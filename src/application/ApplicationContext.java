package application;

@SuppressWarnings("unused")
public class ApplicationContext {
	final private String version = "1.0";
	private String departmentsFile = "data/departments.txt";
	private String movementsFile = "data/movements.txt";
	private String outputFile = "data/patients.inc";
	private String iniFile = "data/patients.ini";
	private String clockFile = "data/clock.inc";

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

}
