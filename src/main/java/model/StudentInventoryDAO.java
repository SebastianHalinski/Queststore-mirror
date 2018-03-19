package model;

import dao.InventoryDAO;
import enums.Table;

import java.sql.Connection;

public class StudentInventoryDAO extends InventoryDAO<StudentInventory> {

    StudentInventoryDAO(Connection connection) {
        super(connection);
    }

    protected void setDefaultTable(){
        this.DEFAULT_TABLE = Table.STUDENTS_ARTIFACTS.getName();
    }
}