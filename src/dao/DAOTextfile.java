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
			PrintWriter output = new PrintWriter("data/usb.pov"); // POVRay-Datei
			/*
			 * Output-Test for POVRay
			 */
			output.print("//=========================\n//Four makros for smooth start and stop\n"
					+ "#macro Cos_01(X)\n\t(0.5 - 0.5 * cos(pi * X))\n#end\n//————————————-------------\n"
					+ "#macro Cos_02(X)\n\t(0.5 - 0.5 * cos(2 * pi * X))\n#end\n//————————————-------------\n"
					+ "#macro Cos_03(X)\n\t(1 - (0.5 - 0.5 * cos(pi * X)))\n#end\n//————————————-------------\n"
					+ "#macro Cos_01(X)\n\t(1 - (0.5 - 0.5 * cos(2 * pi * X)))\n#end\n//————————————-------------\n"
					+ "\n//=========================\n//The moving spheres (patients)\nunion {\n\n");
			/*
			 * End of output-Test for POVRay
			 */

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
					/*
					 * Consoel-output
					 */ 
					 /* System.out.print(movement.whereAmI().getxCoordinate() +
					 * "\t" + movement.whereAmI().getyCoordinate() + "\t" +
					 * movement.whereAmI().getzCoordinate() + "\t" +
					 * movement.whereDoIGo().getxCoordinate() + "\t" +
					 * movement.whereDoIGo().getyCoordinate() + "\t" +
					 * movement.whereDoIGo().getzCoordinate() + "\t" +
					 * movement.whenDoIStart() + "\t" + movement.howDoIMove() +
					 * "\n" + searchFirstDate(movements) );
					 */
					
					/*
					 * Output-Test for POVRay
					 */
					output.print("\t//Sphere {\n\t\t<"
							+ movement.whereAmI().getxCoordinate()
							+ ", "
							+ movement.whereAmI().getyCoordinate()
							+ ", "
							+ movement.whereAmI().getzCoordinate()
							+ ">, 5\n\n"
							+ "\t\ttexture {\n\t\t\tpigment {\n\t\t\t\trgb <1, 0, 0>\n\t\t\t}\n\n\t\t\tfinish"
							+ "{\n\t\t\t\tdiffuse 0.9\n\t\t\t\tphong 1\n\t\t\t}\n\t\t}//End of texture\n\n"
							+ "\t\t//translate <"
							+ movement.whereAmI().getxCoordinate()
							+ ", "
							+ movement.whereAmI().getyCoordinate()
							+ ", "
							+ movement.whereAmI().getzCoordinate()
							+ ">"
							+ "\n\t\t//translate <"
							+ movement.whereAmI().getxCoordinate()
							+ " * Cos_01(clock), "
							+ movement.whereAmI().getyCoordinate()
							+ " * Cos_01(clock), "
							+ movement.whereAmI().getzCoordinate()
							+ " * Cos_01(clock)>\n\t}"
							+ "//End of sphere\n\t//-------------------------\n\n");
					/*
					 * End of output-Test for POVRay
					 */
				}
			}
			
			// print out first and last date of list
			System.out.println(searchFirstDate(movements));
			System.out.println(searchLastDate(movements));

			output.print("}\n//End of union\n//========================="); // POVRay-Datei
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

	// first Date
	public Date searchFirstDate(ArrayList movements) {
		Date firstDate = null;
		Movement movement;

		for (int i = 0; i < movements.size(); i++) {
			movement = (Movement) movements.get(i);
			
			if (i == 0) {
				firstDate = movement.whenDoIStart();
			}

			if (firstDate.after(movement.whenDoIStart())) {
				firstDate = movement.whenDoIStart();
			}
		}

		return firstDate;
	}

	// last Date
	public Date searchLastDate(ArrayList movements) {
		Date lastDate = null;
		Movement movement;

		for (int i = 0; i < movements.size(); i++) {
			movement = (Movement) movements.get(i);
			
			if (i == 0) {
				lastDate = movement.whenDoIStart();
			}

			if (lastDate.before(movement.whenDoIStart())) {
				lastDate = movement.whenDoIStart();
			}
		}

		return lastDate;
	}

}
