package factory;

import dao.AdminDAO;
import model.Admin;

public class AdminFactoryImpl implements UserFactory {
    
    public Admin create(String firstName, String lastName, String password) {

        Admin user = new Admin(firstName, lastName, password);
        int id =  new AdminDAO().saveObjectAndGetId(user);
        user.setId(id);
        return user;
    }
}
