package dao;

import java.util.ArrayList;

import model.Department;
import model.Movement;

public interface DAO {
	public ArrayList<Department> getAllDepartments();

	public ArrayList<Movement> getAllMovements();

}
