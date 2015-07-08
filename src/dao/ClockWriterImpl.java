package dao;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import application.ApplicationContext;

public class ClockWriterImpl implements ClockWriter {
	PrintWriter outputWriter;
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	private static POVWriterImpl uniqueInstance = null;

	public ClockWriterImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		try {
			this.outputWriter = new PrintWriter(
					applicationContext.getClockFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// make sure only one instance of DAOTextfile can get initiated
	public POVWriterImpl getUniqueInstance(ApplicationContext applicationContext) {
		if (uniqueInstance == null) {
			uniqueInstance = new POVWriterImpl(applicationContext);
		}
		return uniqueInstance;
	}

	public void writeOutput(String output) {
		outputWriter.write(output);
	}

	public void close() {
		outputWriter.close();
	}

}
