package application;

import dao.DAO;
import dao.DAOTextfile;
import application.ApplicationContext;

public class Sanatory {

	@SuppressWarnings("unused")
	public Sanatory() {
		final String version = "1.0";
		ApplicationContext applicationContext = new ApplicationContext(version);
		DAO sanatoryDao = new DAOTextfile(applicationContext);
		sanatoryDao.getAllData();
	}

	public static void main(String[] args) {
		new Sanatory();
	}

}
