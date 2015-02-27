package dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import model.Department;
import model.Movement;

public class DAOTextfile implements DAO {
	
	private static DAOTextfile uniqueInstance = null;
	ArrayList<Department> departments = new ArrayList<Department>();
	ArrayList<Movement> movements = new ArrayList<Movement>();
	ArrayList<ArrayList> depMov = new ArrayList<ArrayList>();

	// standard constructor
	public DAOTextfile() {

	}

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
				//System.out.println(department.getID() + "\t" + department.getWeightingFactor() + "\n");
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
					// TODO
				}

				try {
					type = Integer.parseInt(details[3]);
				} catch (NumberFormatException e) {

				}

				Movement movement = new Movement(from, to, entry, type);
				movements.add(movement);
				//System.out.print("Test " + movement.whereAmI().getID() + "\t" + movement.whereDoIGo().getID() + "\n\n");
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

		depMov.add(departments);
		depMov.add(movements);
		return depMov;
	}
	
	public Department findDepartment(String details) {
		Department temp = new Department(0, 0, 0, 0, 0);
		int ID;

		try {
			ID = Integer.parseInt(details);
			//System.out.print(ID);
		} catch (NumberFormatException e) {
			ID = 0;
			//System.out.println("ID = 0");
		}

		if (ID != 0) {
			for (int i = 0; i < departments.size(); i++) {
				if (departments.get(i).getID() == ID) {
					temp = departments.get(i);
				}
			}
		}

		return temp;
	}
}
