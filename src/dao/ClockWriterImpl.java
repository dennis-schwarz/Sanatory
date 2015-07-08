package dao;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import application.ApplicationContext;

public class ClockWriterImpl implements ClockWriter {
	PrintWriter outputWriter;
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	private static ClockWriterImpl uniqueInstance = null;

	public ClockWriterImpl(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		try {
			this.outputWriter = new PrintWriter(
					applicationContext.getOutputFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// make sure only one instance of DAOTextfile can get initiated
	public ClockWriterImpl getUniqueInstance(
			ApplicationContext applicationContext) {
		if (uniqueInstance == null) {
			uniqueInstance = new ClockWriterImpl(applicationContext);
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
