package dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import application.ApplicationContext;
import model.Department;
import model.Movement;

public class DAOTextfile implements DAO {
	// declaration and initialization
	private static DAOTextfile uniqueInstance = null;
	ArrayList<Department> departments = new ArrayList<Department>();
	ArrayList<Movement> movements = new ArrayList<Movement>();
	int spline = 1;
	private ApplicationContext applicationContext;

	public DAOTextfile(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	// make sure only one instance of DAOTextfile can get initiated
	public DAOTextfile getUniqueInstance(ApplicationContext applicationContext) {
		if (uniqueInstance == null) {
			uniqueInstance = new DAOTextfile(applicationContext);
		}
		return uniqueInstance;
	}

	@SuppressWarnings("finally")
	public ArrayList<Department> getAllDepartments() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";

		try {
			// read departments from csv-File
			br = new BufferedReader(new FileReader(
					applicationContext.getDepartmentsFile()));
			line = br.readLine(); // skip first line (title)

			/*
			 * read csv-File line by line and create department object with
			 * properties
			 */
			while ((line = br.readLine()) != null) {
				String[] details = line.split(cvsSplitBy);
				Department department = new Department(
						Integer.parseInt(details[0]), // ID
						Double.parseDouble(details[1]), // xCoordinate
						Double.parseDouble(details[2]), // yCoordinate
						Double.parseDouble(details[3]), // zCoordinate
						Double.parseDouble(details[4])); // weightingFactor
				departments.add(department);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

			// close buffered reader after import
		} finally {
			if (br != null) {

				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return departments;
		}
	}

	@SuppressWarnings("finally")
	public ArrayList<Movement> getAllMovements() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";
		Department from = null;
		Department to = null;
		Date entry = null;
		int type = 0;

		try {
			br = new BufferedReader(new FileReader(
					applicationContext.getMovementsFile()));
			line = br.readLine(); // skip first line (title)

			while ((line = br.readLine()) != null) {
				String[] details = line.split(cvsSplitBy);
				from = checkDepartment(details[0]); // from
				to = checkDepartment(details[1]); // to

				try {
					DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					entry = df.parse(details[2]); // entry
				} catch (ParseException e) {
					e.printStackTrace();
				}

				type = Integer.parseInt(details[3]); // type
				Movement movement = new Movement(from, to, entry, type);
				movements.add(movement);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {

			if (br != null) {
				try {
					br.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return movements;
		}
	}

	// check if department exists
	public Department checkDepartment(String details) {
		int ID = 0;
		Department department = new Department(ID, 0, 0, 0, 0);
		boolean departmentExists = false;

		try {
			ID = Integer.parseInt(details); // is not _ENTRY_ or _EXIT_
		} catch (NumberFormatException e) {
			ID = 0;
		}

		if (ID != 0) {
			for (int i = 0; i < departments.size(); i++) {
				if (departments.get(i).getID() == ID) { // department exists
					department = departments.get(i);
					departmentExists = true;
				}
			}

			// department does not exist
			if (departmentExists == false) {
				department = null;
			}
		}

		return department;
	}
}
