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

@SuppressWarnings({ "rawtypes", "resource" })
public class DAOTextfile implements DAO {
	// declaration and initialization
	private static DAOTextfile uniqueInstance = null;
	ArrayList<Department> departments = new ArrayList<Department>();
	ArrayList<Movement> movements = new ArrayList<Movement>();
	ArrayList<ArrayList> depMov = new ArrayList<ArrayList>();
	int spline = 1;

	public DAOTextfile getUniqueInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DAOTextfile();
		}
		return uniqueInstance;
	}

	@SuppressWarnings("deprecation")
	public ArrayList<ArrayList> getAllData() {	
		// read "departments.txt"
		String csvFile = "data/departments.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); // skip first line (title)

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

		} finally {
			if (br != null) {

				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/*
		 * read "movements.txt"
		 * 
		 * defines first and last date and generates output-file "patients.pov"
		 */
		csvFile = "data/movements.txt";
		br = null;
		Department from = null;
		Department to = null;
		Date entry = null;
		int type = 0;
		Date firstDate = null;
		Date lastDate = null;
		boolean patientHasEntry = false;
		long firstAppear = 0;
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
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

			// define first an last date of all movements
			firstDate = searchFirstDate(movements);
			lastDate = searchLastDate(movements);
			
			// set beginning to midnight
			firstDate.setHours(00);
			firstDate.setMinutes(00);
			firstDate.setSeconds(00);
			
			// calculates the difference in minutes
			long difference = lastDate.getTime() - firstDate.getTime();
			long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
			
			// show only one day (i.e. 20.03.2012 00:00:00)
			showOneDay(firstDate, "20120320000000");
			
			// output-file out of movements-array
			PrintWriter moveOutput = new PrintWriter("data/patients.pov");
			
			// general information for the output-File "patients.pov"
			moveOutput.print("//------------------------------------------------------------------------\n"
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
					+ "#declare Patient =\nsphere {\n\t<1, 1, 1>, 1\n\ttexture {\n\t\tpigment {\n\t\t\t"
					+ "color rgb <0, 1, 0>\n\t\t}\n\t\tfinish {\n\t\t\tambient 0.1\n\t\t\tdiffuse 0.85\n\t\t\t"
					+ "phong 1\n\t\t}\n\t}\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// movements -------------------------------------------------------------\n\n");

			// movements of each patient
			for (int i = 0; i < movements.size(); i++) {
				// department exists
				if (!(movements.get(i).whereAmI() == null || movements.get(i)
						.whereDoIGo() == null)) {
					Date currentDate = movements.get(i).whenDoIStart();
					long diff = currentDate.getTime() - firstDate.getTime(); // time to "arrive" (_ENTRY_)
					long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff); // time in minutes

					// new patient
					if (movements.get(i).whereAmI().getID() == 0) { // _ENTRY_
						moveOutput.print("// spline ----------------------------------------------------------------\n"
								+ "#declare Spline" + spline + " =\nspline {\n\tlinear_spline\n");
						firstAppear = diffInMinutes;
						patientHasEntry = true;
					}

					if (patientHasEntry == true) {
						// as long as patient does not _EXIT_
						if (!(movements.get(i).whereDoIGo().getID() == 0)) {
							// prints the coordinates of the next station
							moveOutput.print("\t" + diffInMinutes + ", <"
									+ movements.get(i).whereDoIGo().getxCoordinate() + ", "
									+ movements.get(i).whereDoIGo().getyCoordinate() + ", "
									+ movements.get(i).whereDoIGo().getzCoordinate() + ">,\n");
						
						} else if (movements.get(i).whereDoIGo().getID() == 0) {
							// patients exists
							patientHasEntry = false;
							
							// generates the patient-objects for the output-file "patients.pov"
							moveOutput.print("}\n// generate patient ------------------------------------------------------\n"
							+ "#if (clock > " + firstAppear + " & clock < " + diffInMinutes + ")\n\t"
							+ "object {\n\t\tPatient\n\t\ttranslate Spline" + spline + "(clock)\n\t}\n#end\n\n");
							
							// increment spline
							spline++;
						}
					}
				}
			}
			
			// output-file for animation
			PrintWriter animationOutput = new PrintWriter("data/patients.ini");
			animationOutput.print(";Persistence Of Vision raytracer version 3.5 example file.\n"
					+ "Antialias = On\n"
					+ "Antialias_Threshold = 0.30\n"
					+ "Antialias_Depth = 3/n"
					+ "Input_File_Name = patients.pov\n"
					+ "Initial_Frame = 1\n"
					+ "Final_Frame = 1000\n"
					+ "Initial_Clock = 0\n" // or showOneDay(firstDate, "20120320000000)
					+ "Final_Clock = " + differenceInMinutes + ";total of minutes\n" // oneDay + 1439
					+ "Cyclic_Animation = on\n"
					+ "Pause_when_Done = off");

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

	// check if department exists
	public Department checkDepartment(String details) {
		int ID = 0;
		Department department = new Department(ID, 0, 0, 0, 0);
		boolean departmentExists = false;

		try {
			ID = Integer.parseInt(details); // department is not _ENTRY_ or _EXIT_
		} catch (NumberFormatException e) {
			ID = 0;
		}

		if (ID != 0) {
			for (int i = 0; i < departments.size(); i++) {
				if (departments.get(i).getID() == ID) { // checks if department exists in "departments.txt"
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

	// show one day
	public long showOneDay(Date firstDate, String definedDate) {

		Date tempDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			tempDate = df.parse(definedDate); // which day should be shown (i.e. 20120320000000)
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diffDefined = tempDate.getTime() - firstDate.getTime(); // start time of the selected day
		long diffInMinutesDefined = TimeUnit.MILLISECONDS.toMinutes(diffDefined); // start time in minutes

		return diffInMinutesDefined;
	}

}
