package application;

import users.UsersDAO;
import users.LogableDAO;
// import users.UserCtrl;
import users.StudentModel;
import users.AdminModel;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

// testImports:
import users.MentorDAO;
import users.MentorModel;
import users.AdminDAO;
import users.AdminModel;
import users.StudentDAO;
import users.StudentModel;
import item.ItemsDAO;

public class RootController{

    private LogableDAO dao;
    private RootView view;
    // private UserCtrl loggedUser;
    public RootController()
    {
        dao = new UsersDAO();
        view = new RootView();
    }

    public void runApplication()
    {
        runTest();
    }

    private void runTest(){
        // view.displayIntro();
        // view.displayLoginScreen();
        // StudentModel student = new StudentModel("Jarek", "Kucharczyk", "123");
        // String message = student.getUserFirstName()+", role: " + student.getUserRole();
        // view.displayMessage(message);
        // view.handlePause();
        // view.displayLogoutScreen();
        // view.displayOutro();
        // System.out.println(String.valueOf(dao.checkIfFileExist()));
        // List<String> list = dao.getDataFromFile();
        // String[] array = new String[list.size()];
        // list.toArray(array);
        // view.displayElementsOfCollection(array);
        // list.add("Lolo na koniec!");
        // dao.saveData(list);
        // AdminModel admin0 = dao.createFirstAdmin();
        // view.displayMessage(admin0.getUserFirstName());

        /// mentors test:
        MentorDAO mentorDao = new MentorDAO();
        List<MentorModel> mentors = mentorDao.getTestMentors();
        view.displayMessage("Mentors");
        for(MentorModel mentor : mentors){
            System.out.println(" -"+mentor);
        }

        /// admins test:
        AdminDAO adminDao = new AdminDAO();
        List<AdminModel> admins = adminDao.getTestAdmins();
        view.displayMessage("Admins");
        for(AdminModel admin : admins){
            System.out.println(" -"+admin);
        }

        ///
        /// admins test:
        StudentDAO studentDao = new StudentDAO();
        List<StudentModel> students = studentDao.getTestStudents();
        view.displayMessage("Students");
        for(StudentModel student : students){
            System.out.println(" -"+student);
        }
        ItemsDAO itemsDao = new ItemsDAO();
    }
}
