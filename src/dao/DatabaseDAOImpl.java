package dao;

import enums.FilePath;
import factory.ConnectionFactory;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileNotFoundException;
import java.io.File;

public class DatabaseDAOImpl extends DAO implements DatabaseDAO{

    private final static String SQL_SCRIPT_PATH = FilePath.SQL_SCRIPT.getPath();
    private static final String DATA_BASE_PATH = FilePath.DATA_BASE.getPath();

    protected Connection connection = null;

    public DatabaseDAOImpl() {
        prepareFile(DATA_BASE_PATH);
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void prepareDatabase() {
        try {
            if (!isConnected()) {
                connection = ConnectionFactory.getConnection();
            }
            updateDatabase();
        } catch (Exception e){
            if (! isConnected()) {
                closeConnection();
            }
        }
    }

    private void updateDatabase(){
        if(connection == null){
            openConnection();
        }
        File sqlFile = new File(SQL_SCRIPT_PATH);
        executeSqlScript(sqlFile);
    }

    public void updateDatabase(String sqlScriptPath){
        if(connection == null){
            openConnection();
        }
        File sqlFile = new File(sqlScriptPath);
        executeSqlScript(sqlFile);
    }

    protected void openConnection() {
        connection = ConnectionFactory.getConnection();
    }

    private boolean isConnected() {
        return connection != null;
    }

    private void executeSqlScript(File inputFile){
        String delimiter = ";";
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile).useDelimiter(delimiter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Statement currentStatement = null;
        while(scanner.hasNext()) {
            String rawStatement = scanner.next() + delimiter;
            try {
                currentStatement = connection.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }
}
