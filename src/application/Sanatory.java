package application;

import dao.DAO;
import dao.DAOTextfile;
import application.ApplicationContext;

public class Sanatory {

	@SuppressWarnings("unused")
	public Sanatory() {
		final String version = "0.1";
		ApplicationContext applicationContext = new ApplicationContext(version);
		DAO sanatoryDao = new DAOTextfile();
		sanatoryDao.getAllData();
	}

	public static void main(String[] args) {
		new Sanatory();
	}

}
