package application;

@SuppressWarnings("unused")
public class ApplicationContext {
	private String version;
	private String departmentsFile;
	private String movementsFile;

	public ApplicationContext(String version) {
		this.version = version;
		this.departmentsFile = "data/departments.txt";
		this.movementsFile = "data/movements.txt";
	}

	public String getMovementsFile() {
		return movementsFile;
	}

	public void setMovementsFile(String movementsFile) {
		this.movementsFile = movementsFile;
	}

	public String getDepartmentsFile() {
		return departmentsFile;
	}

	public void setDepartmentsFile(String departmentsFile) {
		this.departmentsFile = departmentsFile;
	}
}
