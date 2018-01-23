package application;

import java.util.List;
import users.LogableDAO;
import users.UsersDAO;
import users.MentorModel;
import users.AdminModel;
import users.StudentModel;
import users.MentorController;
import users.AdminController;
import users.StudentController;
import java.io.Console;


public class RootController{

    private LogableDAO dao;
    private RootView view;

    public RootController(){
        dao = new UsersDAO();
        dao.prepareAdmin();
        view = new RootView();
    }

    public void runApplication(){
        boolean isDone = false;
        while (! isDone){
            handleIntro();
            String userInput = view.displayLoginScreen("Please, choose an option: ");
            switch (userInput)
            {
                case "1":
                    String [] userData = loggingProcedure();
                    handleUserData(userData);
                    break;
                case "0":
                    isDone = true;
                    handleOutro();
            }
        }
    }

    private String [] loggingProcedure() {
        String login = view.displayLoginScreen("Login: ");
        Console console = System.console();
        System.out.println("Please enter your password: ");
        char[] password = console.readPassword();
        dao.updateLoadedTables();
        String [] userDate = dao.importUserData(login, String.valueOf(password));
        return userDate;
    }

    private void handleUserData(String [] userData) {
        if(userData.length == 0) {
            view.displayMessage("Invalid login or password! Try again!");
            view.handlePause();
        }
        else {
            createUser(userData);
        }
    }

    public void createUser(String [] userData) {
        int ROLE_INDEX = 0;
        if(userData[ROLE_INDEX].equals("admin")){
            AdminModel admin = dao.createAdminModel(userData);
            AdminController adminController = new AdminController(admin);
            adminController.handleMainMenu();
        }
        else if(userData[ROLE_INDEX].equals("mentor")){
            MentorModel mentor = dao.createMentorModel(userData);
            MentorController mentorController = new MentorController(mentor);
            mentorController.handleMainMenu();
        }
        else if(userData[ROLE_INDEX].equals("student")){
            StudentModel student = dao.createStudentModel(userData);
            StudentController studentController = new StudentController(student);
            studentController.handleMainMenu();
        }
    }

    private void handleIntro(){
        IntroOutroDAO ioDao = new IntroOutroDAO();
        List<String> introData = ioDao.getRawDataFromFile();
        view.displayIntro(introData);
    }

    private void handleOutro(){
        IntroOutroDAO ioDao = new IntroOutroDAO();
        List<String> outroData = ioDao.getRawDataFromFile("DataFiles/outro.txt");
        view.displayOutro(outroData);
    }

    private void runTest(){
        System.out.println("Nothing to test...");

    }

}