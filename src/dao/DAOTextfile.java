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

@SuppressWarnings({ "rawtypes" })
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
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";
		
		// read "departments.txt"
		String csvFile = "data/departments.txt";

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
		 * defines first and last date and generates output-files "patients.pov" and "patients.ini"
		 */
		csvFile = "data/movements.txt";
		br = null;
		Department from = null;
		Department to = null;
		Date entry = null;
		int type = 0;
		Date firstDate = null;
		Date lastDate = null;
		boolean patientEntry = false; // patient has entry
		long firstEntry = 0; // entry-time of a patient
		boolean patientIsInHospital = false; // patient is currently in the hospital
		boolean patientMoves = false; // patient moves to departments that exist
		
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
			
			// set firstDate and lastDate to midnight
			firstDate.setHours(00);
			firstDate.setMinutes(00);
			firstDate.setSeconds(00);
			lastDate.setHours(23);
			lastDate.setMinutes(59);
			lastDate.setSeconds(59);

			// calculates the difference in minutes
			long difference = lastDate.getTime() - firstDate.getTime();
			long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
			
			// POVRay output-File out of movements-array
			PrintWriter moveOutput = new PrintWriter("data/patients.pov");
			
			// general information for the POVRay output-File "patients.pov"
			moveOutput.print("//------------------------------------------------------------------------\n"
					+ "// POV-Ray 3.7 Scene File \"patients.pov\"\n// created by Miriam Scholer, Dennis Schwarz and"
					+ " Manuel Huerbin, FHNW, 2015\n\n"
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
					+ "#declare Patient =\nsphere {\n\t<1, 1, 1>, 2\n\ttexture {\n\t\tpigment {\n\t\t\t"
					+ "color rgb <0, 1, 0>\n\t\t}\n\t\tfinish {\n\t\t\tambient 0.1\n\t\t\tdiffuse 0.85\n\t\t\t"
					+ "phong 1\n\t\t}\n\t}\n}\n\n"
					+ "//------------------------------------------------------------------------\n"
					+ "// movements -------------------------------------------------------------");
			
			// movements
			for (int i = 0; i < movements.size(); i++) {
				Date currentDate = movements.get(i).whenDoIStart();
				long diff = currentDate.getTime() - firstDate.getTime(); // time to "arrive" (_ENTRY_)
				long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff); // arriving-time in minutes
				
				// _ENTRY_
				if (movements.get(i).whereAmI() != null && movements.get(i).whereAmI().getID() == 0) {
					patientEntry = true;
					firstEntry = diffInMinutes;
					patientIsInHospital = true;					
				}
			
				// movements
				if (patientIsInHospital) {
					
					// _ENTRY_-header
					if (patientEntry) {
						moveOutput.print("\n\n// spline ----------------------------------------------------------------\n"
								+ "#declare Spline" + spline + " =\nspline {\n\tlinear_spline\n");
						patientEntry = false;	
					}
					
					// patient-movements
					if (movements.get(i).whereAmI() != null && movements.get(i).whereDoIGo() != null
							&& movements.get(i).whereDoIGo().getID() != 0) {
						
						if (movements.get(i).whereAmI().getID() != 0) {
							// time when patient has to leave the current station (method: calculateLeavingTime(...))
							moveOutput.print(
									"\t" + calculateLeavingTime(diffInMinutes, movements.get(i).whereAmI().getxCoordinate(), 
											movements.get(i).whereAmI().getyCoordinate(),
											movements.get(i).whereAmI().getzCoordinate(),
											movements.get(i).whereDoIGo().getxCoordinate(),
											movements.get(i).whereDoIGo().getyCoordinate(),
											movements.get(i).whereDoIGo().getzCoordinate()) 
									+ ", <"
									+ movements.get(i).whereAmI().getxCoordinate() + ", "
									+ movements.get(i).whereAmI().getyCoordinate() + ", "
									+ movements.get(i).whereAmI().getzCoordinate() + ">,\n"
							);
						}
						
						moveOutput.print(
								// when patient arrives at station
								"\t" + diffInMinutes + ", <"
								+ movements.get(i).whereDoIGo().getxCoordinate() + ", "
								+ movements.get(i).whereDoIGo().getyCoordinate() + ", "
								+ movements.get(i).whereDoIGo().getzCoordinate() + ">,\n");
						
						patientMoves = true;
				
					// patient-movement if patient only moves to departments that does not exist	
					} else if (patientMoves == false) {
						moveOutput.print("\t\t-1, <92, 183, 16.25>,\n");
					}
					
					// _EXIT_
					if (movements.get(i).whereDoIGo() != null && movements.get(i).whereDoIGo().getID() == 0) {
						moveOutput.print("}\n// generate patient ------------------------------------------------------\n"
								+ "#if (clock >= " + firstEntry + " & clock < " + diffInMinutes + ")\n\t"
								+ "object {\n\t\tPatient\n\t\ttranslate Spline" + spline + "(clock)\n\t}\n#end");
						patientIsInHospital = false;
						patientMoves = false;
						spline++;
					}
				}
			}	

			moveOutput.close();
			
			// POVRay output-File "patients.ini" for the animation
			PrintWriter animationOutput = new PrintWriter("data/patients.ini");
			animationOutput.print(";Persistence Of Vision raytracer version 3.5 example file.\n"
					+ "Antialias = On\n"
					+ "Antialias_Threshold = 0.30\n"
					+ "Antialias_Depth = 3\n"
					+ "Input_File_Name = patients.pov\n"
					+ "Initial_Frame = 1\n"
					+ "Final_Frame = 1000\n"
					+ "Initial_Clock = 0\n" // or + showOneDay(firstDate, "20120320000000") + "\n"
					+ "Final_Clock = " + differenceInMinutes + ";total of minutes\n" // and "oneDay + 1439"
					+ "Cyclic_Animation = on\n"
					+ "Pause_when_Done = off");
			
			animationOutput.close();

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
			tempDate = df.parse(definedDate); // which day should be shown (i.e. "20120320000000")
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diffDefined = tempDate.getTime() - firstDate.getTime(); // start time of the selected day
		long diffInMinutesDefined = TimeUnit.MILLISECONDS.toMinutes(diffDefined); // this start time in minutes

		return diffInMinutesDefined;
	}
	
	// calculate the time, when a patient has to leave its station
	public long calculateLeavingTime(long diffInMinutes, double currentXCoordinate, double currentYCoordinate,
			double currentZCoordinate, double xDestination, double yDestination, double zDestination) {
		
		double xDistance = 0;
		double yDistance = 0;
		double zDistance = 0;
		double totalDistance = 0;
		
		// calculate distance with only positive coordinates
		if (currentXCoordinate < 0) {
			currentXCoordinate = currentXCoordinate * -1;
		}
		
		if (currentYCoordinate < 0) {
			currentYCoordinate = currentYCoordinate * -1;
		}
		
		if (currentZCoordinate < 0) {
			currentZCoordinate = currentZCoordinate * -1;
		}
		
		if (xDestination < 0) {
			xDestination = xDestination * -1;
		}
		
		if (yDestination < 0) {
			yDestination = yDestination * -1;
		}
		
		if (zDestination < 0) {
			zDestination = zDestination * -1;
		}
		
		// distances between x, y and z
		xDistance = currentXCoordinate - xDestination;
		yDistance = currentYCoordinate - yDestination;
		zDistance = currentZCoordinate - zDestination;

		// squares (result is positive)
		xDistance = xDistance * xDistance;
		yDistance = yDistance * yDistance;
		zDistance = zDistance * zDistance;
		
		// radical of total distance (vector)
		totalDistance = xDistance + yDistance + zDistance;
		totalDistance = Math.sqrt(totalDistance);
		
		// for each step in the coordinate system the patient needs to leave 0.0833 minutes (5 seconds) earlier
		diffInMinutes = (long) (diffInMinutes - (totalDistance * 0.0833));		
		
		return diffInMinutes;
	}

}
