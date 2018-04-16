package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import factory.GeneralModelFactory;
import model.MentorFactoryImpl;
import model.Admin;
import model.Mentor;
import model.Student;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.AdminView;



public class AdminController extends UserControllerImpl implements HttpHandler {

    private Admin admin;
    private AdminView view;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = " ";
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/admin.twig");
        JtwigModel model = JtwigModel.newModel();

        if (method.equals("GET")) {
            response = template.render(model);
        }
        if (method.equals("POST")) {
            //actions
            response = "PIWO";
        }
        sendResponse(response, httpExchange);
    }

    private void sendResponse(String response, HttpExchange httpExchange) {
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeMainMenu(){

        boolean isDone = false;
        while(! isDone) {
            String[] correctChoices = {"1", "2", "3", "4", "5", "6", "7", "0"};
            view.clearScreen();
            view.displayMenu();
            String userChoice = view.getMenuChoice(correctChoices);
            view.clearScreen();

            switch(userChoice) {
                case "1":
                    showProfile(admin);
                    break;
                case "2":
                    createMentor();
                    break;
                case "3":
                    editMentor();
                    break;
                case "4":
                    displayMentorProfile();
                    break;
                case "5":
                    displayStudentsByMentor();
                    break;
                case "6":
                    SchoolController.createNewGroup();
                    break;
                case "7":
                    runExpLevelManager();
                    break;
                case "0":
                    isDone = true;
                    break;
            }
            view.handlePause();
        }
    }

    private void createMentor(){
        String firstName = view.getUserInput("Enter first name: ");
        String lastName = view.getUserInput("Enter last name: ");
        String password = view.getUserInput("Enter password: ");
        Mentor mentor = GeneralModelFactory.getByType(MentorFactoryImpl.class)
                                .create(firstName, lastName, password);
        view.clearScreen();
        view.displayMessageInNextLine("Mentor created: \n");
        view.displayUserWithDetails(mentor);
    }

    private void editMentor() {
        Mentor mentor = SchoolController.getMentorByUserChoice();
        if (mentor != null) {
            boolean isFinished = false;
            while (!isFinished) {
                view.clearScreen();
                view.displayEditMenu();
                String userChoice = view.getUserInput("Select an option: ");
                view.clearScreen();
                view.displayMessage("Mentor to edit:\n");
                view.displayUserWithDetails(mentor);
                view.drawNextLine();
                switch (userChoice) {
                    case "1":
                        String firstName = view.getUserInput("Enter first name: ");
                        mentor.setFirstName(firstName);
                        break;
                    case "2":
                        String lastName = view.getUserInput("Enter last name: ");
                        mentor.setLastName(lastName);
                        break;
                    case "3":
                        String password = view.getUserInput("Enter password: ");
                        mentor.setPassword(password);
                        break;
                    case "4":
                        String email = view.getUserInput("Enter email: ");
                        mentor.setEmail(email);
                        break;
                    case "5":
                        SchoolController.assignMentorToGroup(mentor);
                        break;
                    case "0":
                        isFinished = true;
                        break;
                }
                if (!isFinished) {
                    view.clearScreen();
                    view.displayMessageInNextLine("Mentor`s data:\n");
                    view.displayUserWithDetails(mentor);
                    view.handlePause();
                }
            }
        }
    }

    private void displayMentorProfile() {
        Mentor mentor = SchoolController.getMentorByUserChoice();
        if(mentor != null) {
            view.clearScreen();
            view.displayMessageInNextLine("Mentor's details:\n");
            view.displayUserWithDetails(mentor);
        }
    }

    private void displayStudentsByMentor() {
        Mentor mentor = SchoolController.getMentorByUserChoice();
        if(mentor != null) {
            view.clearScreen();
            view.displayMessageInNextLine("Students:\n");
            List<Student> students = SchoolController.getStudentsByGroup(mentor.getGroup());
            view.displayObjects(students);
        }
    }

    private void runExpLevelManager(){
        ExperienceLevelsController.getInstance().manageExperienceLevels();
    }
}
