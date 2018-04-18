package dao;

import controllers.UserController;
import exceptions.LoginFailure;
import model.User;


public interface LoginDAO extends SpecialDAO {

    User getUserByLoginAndPassword(String login, String password) throws LoginFailure;
}




