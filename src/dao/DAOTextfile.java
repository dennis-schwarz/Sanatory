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
import java.util.concurrent.TimeUnit;

import model.Department;
import model.Movement;

@SuppressWarnings("rawtypes")
public class DAOTextfile implements DAO {
	// declaration and initialization
	private static DAOTextfile uniqueInstance = null;
	ArrayList<Department> departments = new ArrayList<Department>();
	ArrayList<Movement> movementsAdmin = new ArrayList<Movement>(); // to find number of lines and first and last date
	ArrayList<Movement> movements = new ArrayList<Movement>();
	ArrayList<ArrayList> depMov = new ArrayList<ArrayList>();
	int splineCounter = 2;
	int lineCounterBeginning = 0;
	int lineCounterCheck = 0;

	// constructor
	public DAOTextfile getUniqueInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DAOTextfile();
		}
		return uniqueInstance;
	}

	// Datenverarbeitung Departments
	@SuppressWarnings({ "resource", "unused" })
	public ArrayList<ArrayList> getAllData() {
		String csvFileDepartments = "data/departments.txt"; // departments einlesen
		String csvFileMovements = "data/movements.txt";
		String csvFilePOV = "data/movements.txt";
		BufferedReader brDepartments = null;
		BufferedReader brMovements = null;
		BufferedReader brPOV = null;
		String cvsSplitBy = "\t";

		try {
			brDepartments = new BufferedReader(new FileReader(csvFileDepartments));
			String lineDepartments = brDepartments.readLine();
			while ((lineDepartments = brDepartments.readLine()) != null) {
				// use comma as separator
				String[] details = lineDepartments.split(cvsSplitBy);
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
			if (brDepartments != null) {

				try {
					brDepartments.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// Datenverarbeitung Departments fertig
		
		// Datenverarbeitung Movements first
		Date entry = null;
		Date firstDate = null;
		Date lastDate = null;

		try {
			// zuerst einmal "movementsTest.txt"-File einlesen, um Anzahl Zeilen
			// und das erste und letzte Datum zu bestimmen (fuer Einteilung fuer
			// POVRay)
			brMovements = new BufferedReader(new FileReader(csvFileMovements));
			String lineMovements = brMovements.readLine();
			int type = 0;

			while ((lineMovements = brMovements.readLine()) != null) {
				lineCounterBeginning++;
				String[] searchDates = lineMovements.split(cvsSplitBy);
				Department from = findDepartment(searchDates[0]);
				Department to = findDepartment(searchDates[1]);
				
				try {
					DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					entry = df.parse(searchDates[2]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			
				try {
					type = Integer.parseInt(searchDates[3]);
				} catch (NumberFormatException e) {

				}

				Movement movementCurrent = new Movement(from, to, entry, type);
				movements.add(movementCurrent);
				movementsAdmin.add(movementCurrent);
			}
			
			firstDate = searchFirstDate(movements);
			lastDate = searchLastDate(movements);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {

			if (brMovements != null) {
				try {
					brMovements.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// Datenverarbeitung Movements first fertig
		
		// movements.txt ein zweitesMal lesen und POVRay-File generieren
		try {
			brPOV = new BufferedReader(new FileReader(csvFilePOV));
			String linePOV = brPOV.readLine();
			PrintWriter output = new PrintWriter("data/usb.pov");

			output.print("//------------------------------------------------------------------------\n"
					+ "// POV-Ray 3.7 Scene File \"patients.pov\"\n// created by Manuel Huerbin, Dennis Schwarz and "
					+ "Miriam Scholer, FHNW, 2015\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// general settings ------------------------------------------------------\n"
					+ "#version 3.7;\nglobal_settings {\n\tassumed_gamma 1.0\n}\n"
					+ "#include \"colors.inc\"\n#include \"textures.inc\"\n#include \"math.inc\"\n#include "
					+ "\"transforms.inc\"\n#include \"usb.pov\" //usb-model (transparent)\n"
					+ "\n//------------------------------------------------------------------------\n"
					+ "// camera ----------------------------------------------------------------\n"
					+ "#declare Camera =\ncamera {\n\tperspective\n\tup <0, 1, 0>\n\tright -x * image_width / image_height"
					+ "\n\tlocation <0, 0, 1092.539>\n\tlook_at <0, 0, 1091.539>\n\tangle 22.34049 //horizontal FOV angle"
					+ "\n\trotate <0, 0, -33.51162> //roll\n\trotate <44.54607, 0, 0> //pitch"
					+ "\n\trotate <0, -44.05138, 0> //yaw\n\ttranslate <201.5, 233.35, 32.205>\n}\n"
					+ "camera {\n\tCamera\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// background ------------------------------------------------------------\n"
					+ "background {\n\tcolor srgb <1, 1, 1>\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// sun -------------------------------------------------------------------\n"
					+ "light_source {\n\t<-1000, 2500, -2500>\n\tcolor <1, 1, 1>\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// sky -------------------------------------------------------------------\n"
					+ "sky_sphere {\n\tpigment {\n\t\tgradient <0, 0, 0>\n\t\tcolor_map {\n\t\t\t"
					+ "[0 color rgb <1, 1, 1>]\n\t\t\t[1 color rgb <1, 2, 3>]\n\t\t}\n\t\tscale 2\n\t}\n}\n\n"
					+ "//-----------------------------------------------------------------------\n"
					+ "// patient --------------------------------------------------------------\n"
					+ "#declare Patient =\nsphere {\n\t<1, 1, 1>, 3\n\ttexture {\n\t\tpigment {\n\t\t\t"
					+ "color rgb <0, 1, 0>\n\t\t}\n\t\tfinish {\n\t\t\tambient 0.1\n\t\t\tdiffuse 0.85\n\t\t\t"
					+ "phong 1\n\t\t}\n\t}\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// splines ---------------------------------------------------------------\n"
					+ "#declare Spline1 =\nspline {\n\tnatural_spline\n");

			while ((linePOV = brPOV.readLine()) != null) {
				lineCounterCheck++;
				// use comma as separator
				String[] details = linePOV.split(cvsSplitBy);
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
					Date currentDate = movement.whenDoIStart();
					long diff = currentDate.getTime() - firstDate.getTime();
					long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);
							
					output.print(diffInHours / 1000.00 + ", <" + movement.whereDoIGo().getxCoordinate() + ", "
							+ movement.whereDoIGo().getyCoordinate() + ", "
							+ movement.whereDoIGo().getzCoordinate() + ">,\n");

					// separates "patients" (by "exit") and checks the last
					// patient
					if (to.getID() == 0
							&& lineCounterCheck < lineCounterBeginning - 1) {
						output.print("}\n#declare Spline" + splineCounter
								+ " =\nspline {\n\tnatural_spline\n");
						splineCounter++;
					}
				}
			}

			output.print("}\n\n//------------------------------------------------------------------------"
					+ "\n// loop ------------------------------------------------------------------"
					+ "\n#declare Start = 0; //start\n#declare End = 1; //end\n#while (Start < End)\n");

			for (int i = 1; i < splineCounter; i++) {
				output.print("\tobject {\n\t\tPatient\n\t\ttranslate Spline"
						+ i + "(mod((clock + Start / End), 5))" + "\n\t}\n");
			}

			output.print("\t#declare Start = Start + 1; //steps\n#end");

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {

			if (brPOV != null) {
				try {
					brPOV.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		depMov.add(departments);
		depMov.add(movements);
		return depMov;
	}

	// find department
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
