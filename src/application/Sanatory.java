package application;

import dao.DAO;
import dao.DAOTextfile;
import application.ApplicationContext;

public class Sanatory {
	
	public Sanatory(){
		final String version = "0.1";
		ApplicationContext applicationContext = new ApplicationContext(version);
		DAO sanatoryDao = new DAOTextfile();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Sanatory();
		System.out.println("Test");
	}

}
