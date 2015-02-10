package dao;

import java.util.ArrayList;
import java.util.Collection;

import model.Department;

public interface DAO {
	public ArrayList<ArrayList> getAllData();

	public Department findDepartment(String details);
}
