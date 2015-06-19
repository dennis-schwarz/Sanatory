package application;

@SuppressWarnings("unused")
public class ApplicationContext {
	final private String version = "1.0";
	private String departmentsFile = "data/departments.txt";
	private String movementsFile = "data/movements.txt";
	private String outputFile = "data/patients.pov";
	private String iniFile = "data/patients.ini";

	public String getOutputFile() {
		return outputFile;
	}

	public String getIniFile() {
		return iniFile;
	}

	public ApplicationContext() {
	}

	public String getMovementsFile() {
		return movementsFile;
	}

	public String getDepartmentsFile() {
		return departmentsFile;
	}
}
