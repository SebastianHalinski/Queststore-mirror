package dao;

import controllers.*;
import enums.Table;
import exceptions.LoginFailure;
import factory.UserControllerFactory;
import managers.SQLProcessManager;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAOImpl implements LoginDAO {

    private Connection connection;

    LoginDAOImpl(Connection connection) {
        this.connection = connection;
    }

    public User getUserByLoginAndPassword(String login, String password) throws LoginFailure {
        User user;
        String [] usersTables = {Table.ADMINS.getName(), Table.MENTORS.getName(), Table.STUDENTS.getName()};
        String statement;
        for(String table : usersTables) {
            statement = String.format("SELECT * FROM %s WHERE first_name || id =? AND password=?", table);
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.isBeforeFirst()) {
                    String[] userData = SQLProcessManager.getObjectData(resultSet);
                    user = extractUser(userData, table);
                    if (user != null) {
                        return user;
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        throw new LoginFailure();
    }

    private User extractUser(String[] userData, String table) {
        User user = null;
        switch (table) {
            case ("admins"):
                user = ModelDaoFactory.getByType(AdminDAO.class).extractModel(userData);
                break;
            case ("mentors"):
                user = ModelDaoFactory.getByType(MentorDAO.class).extractModel(userData);
                break;
            case ("students"):
                user = ModelDaoFactory.getByType(StudentDAO.class).extractModel(userData);
                break;
        }
        return user;
    }
}
