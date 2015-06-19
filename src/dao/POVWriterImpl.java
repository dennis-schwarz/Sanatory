package dao;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import application.ApplicationContext;

public class POVWriterImpl implements POVWriter {
	PrintWriter outputWriter;
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	private static POVWriterImpl uniqueInstance = null;
	
	public POVWriterImpl(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
		try {
			this.outputWriter = new PrintWriter(applicationContext.getOutputFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Make sure only one Instance of DAOTextfile can get initiated
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
