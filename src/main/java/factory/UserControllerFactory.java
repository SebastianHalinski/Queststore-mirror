package factory;

import controllers.*;
import model.Admin;
import model.Mentor;
import model.Student;
import model.User;

public class UserControllerFactory {

    public static <T extends User> UserController getController(T user) {

        UserController controller = null;
        String userName = user.getClass().getSimpleName();
        switch(userName) {
            case("Admin"):
                Admin admin = (Admin) user;
                controller = new AdminController();
                break;
            case("Mentor"):
                Mentor mentor = (Mentor) user;
                controller = new MentorController();
                break;
            case("Student"):
                Student student = (Student) user;
                controller = new StudentController();
                break;
        }
        return controller;
    }
}
