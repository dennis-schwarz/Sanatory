package dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Department;
import model.Movement;

@SuppressWarnings("rawtypes")
public class DAOTextfile implements DAO {
	private static DAOTextfile uniqueInstance = null;
	ArrayList<Department> departments = new ArrayList<Department>();
	ArrayList<Movement> movements = new ArrayList<Movement>();
	ArrayList<ArrayList> depMov = new ArrayList<ArrayList>();

	public DAOTextfile getUniqueInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DAOTextfile();
		}

		return uniqueInstance;
	}

	public ArrayList<ArrayList> getAllData() {
		String csvFile = "data/departments.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] details = line.split(cvsSplitBy);
				Department department = new Department(
						Integer.parseInt(details[0]),
						Double.parseDouble(details[1]),
						Double.parseDouble(details[2]),
						Double.parseDouble(details[3]),
						Double.parseDouble(details[4]));
				departments.add(department);
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
		}

		csvFile = "data/movements.txt";
		Date entry = null;

		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			PrintWriter output = new PrintWriter("data/output.txt");

			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] details = line.split(cvsSplitBy);
				Department from = findDepartment(details[0]);
				Department to = findDepartment(details[1]);

				int type = 0;
				try {
					DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					entry = df.parse(details[2]);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				try {
					type = Integer.parseInt(details[3]);
				} catch (NumberFormatException e) {

				}

				Movement movement = new Movement(from, to, entry, type);
				movements.add(movement);

				// output (only existing departments)
				if (!(from.getID() == 666 || to.getID() == 666)) {
					output.print(movement.whereAmI().getxCoordinate() + "\t"
							+ movement.whereAmI().getyCoordinate() + "\t"
							+ movement.whereAmI().getzCoordinate() + "\t"
							+ movement.whereDoIGo().getxCoordinate() + "\t"
							+ movement.whereDoIGo().getyCoordinate() + "\t"
							+ movement.whereDoIGo().getzCoordinate() + "\t"
							+ movement.whenDoIStart() + "\t"
							+ movement.howDoIMove() + "\n");
				}
			}

			output.close();

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
		}

		depMov.add(departments);
		depMov.add(movements);
		return depMov;
	}

	public Department findDepartment(String details) {
		Department temp = new Department(0, 0, 0, 0, 0);
		int ID = 0;
		boolean departmentExist = false;

		try {
			ID = Integer.parseInt(details);
		} catch (NumberFormatException e) {
			ID = 0;
		}

		if (ID != 0) {
			for (int i = 0; i < departments.size(); i++) {

				if (departments.get(i).getID() == ID) {
					temp = departments.get(i);
					departmentExist = true;
				}
			}

			// department does not exist (evil department with ID 666)
			if (departmentExist == false) {
				temp = new Department(666, 0, 0, 0, 0);
			}
		}

		return temp;
	}

}
