package control;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dao.ClockWriter;
import dao.ClockWriterImpl;
import dao.DAO;
import dao.DAOTextfile;
import dao.IniWriter;
import dao.IniWriterImpl;
import dao.POVWriter;
import dao.POVWriterImpl;
import application.ApplicationContext;
import model.Department;
import model.Movement;

public class SanatoryController {
	private ApplicationContext applicationContext;
	private DAO sanatoryDao;
	private POVWriter povWriter;
	private IniWriter iniWriter;
	private ClockWriter clockWriter;
	private ArrayList<Department> departments;
	private ArrayList<Movement> movements;
	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList> depMov = new ArrayList<ArrayList>();
	Date firstDate = null;
	Date lastDate = null;
	boolean patientEntry = false; // patient has entry
	double firstEntry = 0; // entry-time of a patient
	boolean patientIsInHospital = false; // patient is currently in the hospital
	boolean patientMoves = false; // patient moves to departments that exist
	double diffInMinutes = 0;
	double lastDiffInMinutes = 0;
	int spline = 1;

	public SanatoryController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.sanatoryDao = new DAOTextfile(applicationContext);
		this.povWriter = new POVWriterImpl(applicationContext);
		this.iniWriter = new IniWriterImpl(applicationContext);
		this.clockWriter = new ClockWriterImpl(applicationContext);
		this.departments = sanatoryDao.getAllDepartments();
		this.movements = sanatoryDao.getAllMovements();
	}

	public void ProcessInput() {
		// define first an last date of all movements
		firstDate = searchFirstDate(movements);
		lastDate = searchLastDate(movements);

		// convert to calendar-format
		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(firstDate);
		Calendar lastCal = Calendar.getInstance();
		lastCal.setTime(lastDate);

		// set firstDate and lastDate to midnight
		firstCal.set(Calendar.HOUR_OF_DAY, 00);
		firstCal.set(Calendar.MINUTE, 00);
		firstCal.set(Calendar.SECOND, 00);
		lastCal.set(Calendar.HOUR_OF_DAY, 23);
		lastCal.set(Calendar.MINUTE, 59);
		lastCal.set(Calendar.SECOND, 59);

		// convert back to date-format (for calculation)
		firstDate = firstCal.getTime();
		lastDate = lastCal.getTime();

		// calculates the difference in minutes
		double difference = lastDate.getTime() - firstDate.getTime();
		@SuppressWarnings("unused")
		double differenceInMinutes = TimeUnit.MILLISECONDS
				.toMinutes((long) difference);

		// choose one day to display:
		long chosedDate = showOneDay(firstDate,
				applicationContext.getOutputDate());

		povWriter
				.writeOutput("//------------------------------------------------------------------------\n"
						+ "// patient ---------------------------------------------------------------\n"
						+ "#declare Patient =\nsphere {\n\t<1, 1, 1>, 2\n\ttexture {\n\t\tpigment {\n\t\t\t"
						+ "color rgb <0, 1, 0>\n\t\t}\n\t\tfinish {\n\t\t\tambient 0.1\n\t\t\tdiffuse 0.85\n\t\t\t"
						+ "phong 1\n\t\t}\n\t}\n}\n\n"
						+ "//------------------------------------------------------------------------\n"
						+ "// movements -------------------------------------------------------------"
						+ "\n#declare patients =\nunion {");

		// movements
		for (int i = 0; i < movements.size(); i++) {

			lastDiffInMinutes = diffInMinutes;

			Date currentDate = movements.get(i).whenDoIStart();
			// time to "arrive" (_ENTRY_)
			long diff = currentDate.getTime() - firstDate.getTime();
			// arriving-time in minutes
			diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

			// _ENTRY_
			if (movements.get(i).whereAmI() != null
					&& movements.get(i).whereAmI().getID() == 0) {
				patientEntry = true;
				firstEntry = diffInMinutes;
				patientIsInHospital = true;
			}

			// movements
			if (patientIsInHospital) {

				// _ENTRY_-header
				if (patientEntry) {
					povWriter
							.writeOutput("\n\n// spline ----------------------------------------------------------------\n"
									+ "#declare Spline"
									+ spline
									+ " =\nspline {\n\tlinear_spline\n");
					patientEntry = false;
				}

				// patient-movements
				if (movements.get(i).whereAmI() != null
						&& movements.get(i).whereDoIGo() != null
						// patient is not leaving hospital
						&& movements.get(i).whereDoIGo().getID() != 0) {

					// calculate optional leaving time
					double leavingTime = calculateLeavingTime(diffInMinutes,
							movements.get(i).whereAmI().getxCoordinate(),
							movements.get(i).whereAmI().getyCoordinate(),
							movements.get(i).whereAmI().getzCoordinate(),
							movements.get(i).whereDoIGo().getxCoordinate(),
							movements.get(i).whereDoIGo().getyCoordinate(),
							movements.get(i).whereDoIGo().getzCoordinate(),
							lastDiffInMinutes);

					/*
					 * leaving time is only written down after the patients
					 * arrived at a station
					 */
					// when it is not the patient's entry
					if (movements.get(i).whereAmI().getID() != 0) {
						/*
						 * time when patient has to leave the current station
						 * (method: calculateLeavingTime(...)) diffInMinutes is
						 * the time, when the patient has to arrive at a station
						 */
						povWriter.writeOutput("\t" + leavingTime + ", <"
								+ movements.get(i).whereAmI().getxCoordinate()
								+ ", "
								+ movements.get(i).whereAmI().getyCoordinate()
								+ ", "
								+ movements.get(i).whereAmI().getzCoordinate()
								+ ">,\n");
					}

					/*
					 * if arrivingTime is equal to leavingTime increase
					 * arrivingTime 1 minute
					 */
					if (leavingTime == diffInMinutes) {
						diffInMinutes = diffInMinutes + 1;
					}

					povWriter.writeOutput(
					// when patient arrives at station
							"\t"
									+ diffInMinutes
									+ ", <"
									+ movements.get(i).whereDoIGo()
											.getxCoordinate()
									+ ", "
									+ movements.get(i).whereDoIGo()
											.getyCoordinate()
									+ ", "
									+ movements.get(i).whereDoIGo()
											.getzCoordinate() + ">,\n");

					patientMoves = true;

					/*
					 * patient-movement if patient only moves to departments
					 * that does not exist
					 */
				} else if (patientMoves == false) {
					povWriter.writeOutput("\t\t-1, <92, 183, 16.25>,\n");
				}

				// _EXIT_
				if (movements.get(i).whereDoIGo() != null
						&& movements.get(i).whereDoIGo().getID() == 0) {
					povWriter
							.writeOutput("}\n// generate patient ------------------------------------------------------\n"
									+ "#if (clock >= "
									+ firstEntry
									+ " & clock < "
									+ diffInMinutes
									+ ")\n\t"
									+ "object {\n\t\tPatient\n\t\ttranslate Spline"
									+ spline + "(clock)\n\t}\n#end");
					patientIsInHospital = false;
					patientMoves = false;
					spline++;
				}
			}
		}
		// close union with "}" and close writer
		povWriter.writeOutput("\n}");
		povWriter.close();

		// write "patients.ini"-file for clock animation
		iniWriter
				.writeOutput(";persistence of vision raytracer version 3.5 example file.\n"
						+ "Antialias = On\n"
						+ "Antialias_Threshold = 0.30\n"
						+ "Antialias_Depth = 3\n"
						+ "Input_File_Name = patients.pov\n"
						+ "Initial_Frame = 1\n"
						+ "Final_Frame = 1000\n"
						+ "Initial_Clock = "
						// show chosedDate
						+ chosedDate
						+ "\n"
						+ "Final_Clock = "
						// until? (2 days)
						+ (chosedDate + 2879)
						+ "\n"
						+ "Cyclic_Animation = on\n" + "Pause_when_Done = off");

		iniWriter.close();

		// write "clock.ini"-file for showing the clock
		clockWriter
				.writeOutput("//------------------------------------------------------------------------\n"
						+ "// clock - textures ------------------------------------------------------\n"
						+ "#declare Frame_Texture =\n\ttexture {\n\t\tpigment {\n\t\t\tcolor rgb <1.0, 1, 1> * 0.1\n\t\t}\n"
						+ "\t\tfinish {\n\t\t\tphong 1\n\t\t\treflection {\n\t\t\t\t0.40\n\t\t\t\tmetallic\n\t\t\t}\n"
						+ "\t\t}\n\t}\n\n#declare H_Texture =\n\ttexture {\n\t\tpigment {\n\t\t\tcolor rgb <1, 1, 1> * 0.0"
						+ "\n\t\t}\n\t}\n\n#declare Min_Texture =\n\ttexture {\n\t\tH_Texture\n\t}\n\n"
						+ "#declare Face_Texture =\n\ttexture {\n\t\tpigment {\n\t\t\tcolor rgb <1, 1, 1> * 1.10\n\t\t}\n"
						+ "\t}\n\n#declare Hands_Texture =\n\ttexture {\n\t\tpigment {\n\t\t\tcolor rgb <1, 1, 1> * 0.0\n"
						+ "\t\t}\n\t}\n\n//------------------------------------------------------------------------\n"
						+ "// clock-size ------------------------------------------------------------\n#declare CR = 30;\n\n"
						+ "//------------------------------------------------------------------------\n"
						+ "// calculation of time ---------------------------------------------------\n"
						+ "#local H = 0;\n#local Min = 0;\n#declare Initial_Clock = "
						+ chosedDate
						+ ";\n#declare Final_Clock "
						+ "= "
						+ (chosedDate + 2879)
						+ ";\n#declare totalTime = Final_Clock - Initial_Clock;\n"
						+ "#declare tempClock = clock - Initial_Clock;\n#declare Clock_Time = (tempClock / 720) + H "
						+ " / (12) + Min / (720);\n\n// rotations of hands (clock) ----------------------------"
						+ "----------------\n"
						+ "#declare Rotate_H = Clock_Time * 360;\n#declare Rotate_Min = Clock_Time * 360 * 12;\n"
						+ "\n//minutes-jump (clock) --------------------------------------------------\n"
						+ "#declare Rotate_Min = int(Rotate_Min / 6 + 0.001) * 6;\n#declare Flat = <1, 1, 0.025>;\n"
						+ "\n// border radii (clock) --------------------------------------------------\n"
						+ "#declare Min_Ro = CR * 0.92;\n#declare Min_Ri = CR * 0.82;\n#declare H_Ri = CR * 0.65;\n"
						+ "#declare Min_R = CR * 0.0200;\n#declare H_R = CR * 0.0400;\n#declare Face_D = 0.001;\n"
						+ "\n// length of hands (clock) -----------------------------------------------\n"
						+ "#declare Hand_H_Len = CR * 0.60;\n#declare Hand_Min_Len = CR * 0.85;\n"
						+ "\n// radii of the hands (clock) --------------------------------------------\n"
						+ "#declare Hand_H_D = CR * 0.055;\n#declare Hand_Min_D = CR * 0.035;\n\n"
						+ "// position z of hands (clock) -------------------------------------------\n"
						+ "#declare Hand_H_Z = CR * 0.05;\n#declare Hand_Min_Z = CR * 0.04;\n\n"
						+ "//------------------------------------------------------------------------\n"
						+ "// view ------------------------------------------------------------------\n"
						+ "union {\n\t// hands -------------------------------------------------------------\n\t"
						+ "// hours\n\tcylinder {\n\t\t<0, 0, 0>, <0, Hand_H_Len, 0>, Hand_H_D\n\t\tscale Flat\n\t\t"
						+ "rotate <0, 0, - Rotate_H>\n\t\ttranslate <0, 0, -Hand_H_Z>\n\t\ttexture {\n\t\t\t"
						+ "Hands_Texture\n\t\t}\n\t\ttranslate <200, 140, 0>\n\t\tno_shadow\n\t}\n\n\t// "
						+ "minutes\n\tcylinder {\n\t\t"
						+ "<0, 0, 0>, <0, Hand_Min_Len, 0>, Hand_Min_D\n\t\tscale Flat\n\t\trotate <0, 0, - Rotate_Min>"
						+ "\n\t\ttranslate <0, 0, -Hand_Min_Z>\n\t\ttexture {\n\t\t\tHands_Texture\n\t\t}\n\t\t"
						+ "translate <200, 140, 0>\n\t\tno_shadow\n\t}\n\n\t"
						+ "// body --------------------------------------------------------------\n\t// center-point\n\t"
						+ "cylinder {\n\t\t<0, 0, -Hand_H_Z>, <0, 0, 0>, Hand_H_D * 1.5\n\t\ttexture {\n\t\t\t"
						+ "Hands_Texture\n\t\t}\n\t\ttranslate <200, 140, 0>\n\t\tno_shadow\n\t}\n\n\t"
						+ "// face\n\tcylinder {\n\t\t<0, 0, 0>, <0, 0, 0.001>, CR\n\t\ttexture {\n\t\t\tFace_Texture"
						+ "\n\t\t}\n\t\ttranslate <200, 140, 0>\n\t\tno_shadow\n\t\tno_shadow\n\t}\n\n"
						+ "// clock-face\n\t#declare Nr = 0;\n\t#while (Nr < 60)\n\t\t"
						+ "#if(Nr / 5 = int(Nr / 5)) // hours\n\t\t\tcylinder {\n\t\t\t\t<0, H_Ri, 0>, <0, Min_Ro, 0>, H_R"
						+ "\n\t\t\t\tscale Flat\n\t\t\t\trotate <0, 0, Nr * 360 / 60>\n\t\t\t\ttexture {\n\t\t\t\t\t"
						+ "H_Texture\n\t\t\t\t}\n\t\t\t\ttranslate <200, 140, 0>\n\t\tno_shadow\n\t\t\t}\n\t\t#else "
						+ "// minutes\n\t\t\t"
						+ "cylinder {\n\t\t\t\t<0, Min_Ri, 0>, <0, Min_Ro, 0>, Min_R\n\t\t\t\tscale Flat\n\t\t\t\t"
						+ "rotate <0, 0, Nr * 360 / 60>\n\t\t\t\ttexture {\n\t\t\t\t\tMin_Texture\n\t\t\t\t}\n\t\t\t\t"
						+ "translate <200, 140, 0>\n\t\tno_shadow\n\t\t\t}\n\t\t#end\n\t\t#declare Nr = "
						+ "Nr + 1;\n\t#end\n}\n\n");
		clockWriter.close();

		depMov.add(departments);
		depMov.add(movements);
	}

	// first Date
	public Date searchFirstDate(ArrayList<Movement> movements) {
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
	public Date searchLastDate(ArrayList<Movement> movements) {
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

	// calculate the time, when a patient has to leave its station
	public double calculateLeavingTime(double diffInMinutes,
			double currentXCoordinate, double currentYCoordinate,
			double currentZCoordinate, double xDestination,
			double yDestination, double zDestination, double lastDiffInMinutes) {

		double xDistance = 0.0;
		double yDistance = 0.0;
		double zDistance = 0.0;
		double totalDistance = 0.0;

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

		/*
		 * for each step in the coordinate system the patient needs to leave 0.5
		 * minutes earlier
		 */
		diffInMinutes = diffInMinutes - (totalDistance * 0.5);

		/*
		 * catch all cases, that would leave the station before they arrive at
		 * this station (increases speed (92 cases))
		 */
		if (diffInMinutes < lastDiffInMinutes) {
			diffInMinutes = lastDiffInMinutes;
		}

		return diffInMinutes;
	}

	// show one day
	public long showOneDay(Date firstDate, String definedDate) {
		Date tempDate = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			// which day should be shown (i.e. "20120320000000")
			tempDate = df.parse(definedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// start time of the selected day
		long diffDefined = tempDate.getTime() - firstDate.getTime();
		long diffInMinutesDefined = TimeUnit.MILLISECONDS
				.toMinutes(diffDefined); // this start time in minutes

		return diffInMinutesDefined;
	}

}
