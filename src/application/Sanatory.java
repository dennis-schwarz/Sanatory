package application;

import control.SanatoryController;

public class Sanatory {
	private ApplicationContext applicationContext;
	private SanatoryController sanatoryController;

	public Sanatory() {
		this.applicationContext = new ApplicationContext();
		this.sanatoryController = new SanatoryController(applicationContext);
		sanatoryController.ProcessInput();
	}

	public static void main(String[] args) {
		new Sanatory();
	}

}
