package controllers;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import factory.GeneralModelFactory;
import model.*;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.AdminView;



public class AdminController extends UserControllerImpl implements HttpHandler {

    private Admin admin;
    private AdminView view;

    public AdminController() {
        this.view = new AdminView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = " ";
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/admin.twig");
        JtwigModel model = JtwigModel.newModel();

        if (method.equals("GET")) {
            response = template.render(model);
            String uri = httpExchange.getRequestURI().toString();
            String adminRoot = "/admin";
            if (uri.startsWith(adminRoot)) {
                if (uri.startsWith("/create_mentor", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/create_mentor.twig");
                    response = template.render(model);
                }

                else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/edit_mentor.twig");
                    response = template.render(model);
                }


                else if (uri.startsWith("/display_mentor", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/display_mentor.twig");
                    response = template.render(model);
                }


                else if (uri.startsWith("/display_students_by_mentor", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/display_students_by_mentor.twig");
                    response = template.render(model);
                }


                else if (uri.startsWith("/create_group", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/create_group.twig");
                    response = template.render(model);
                }


                else if (uri.startsWith("/createexplvl", adminRoot.length())) {
                    template = JtwigTemplate.classpathTemplate("templates/admin/create_explvl.twig");
                    response = template.render(model);
                }
                
                else if (uri.startsWith("/admin", adminRoot.length())) {
                    Headers responseHeaders = httpExchange.getResponseHeaders();
                    responseHeaders.add("Location", "/admin");
                    httpExchange.sendResponseHeaders(302, -1);
                    httpExchange.close();
                    return;
                }

                else if (uri.startsWith("/login", adminRoot.length())) {
                    Headers responseHeaders = httpExchange.getResponseHeaders();
                    responseHeaders.add("Location", "/login");
                    httpExchange.sendResponseHeaders(302, -1);
                    httpExchange.close();
                    return;

                }
            }
        }
        if (method.equals("POST")) {
            //actions
            response = template.render(model);
            String uri = httpExchange.getRequestURI().toString();
            String adminRoot = "/admin";
            if (uri.startsWith(adminRoot)){
                if (uri.startsWith("/create_mentor", adminRoot.length())) {
                    InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String formData = br.readLine();
                    Map inputs = parseFormData(formData);
                    createMentorProcedure(inputs);
                    Headers responseHeaders = httpExchange.getResponseHeaders();
                    responseHeaders.add("Location", "/admin");
                    httpExchange.sendResponseHeaders(302, -1);
                    httpExchange.close();
                    return;
                }

                else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
                    InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String formData = br.readLine();
                    Map inputs = parseFormData(formData);
                    editMentorProcedure(inputs);
                    Headers responseHeaders = httpExchange.getResponseHeaders();
                    responseHeaders.add("Location", "/admin");
                    httpExchange.sendResponseHeaders(302, -1);
                    httpExchange.close();
                    return;
                }

            }
        }
        view.sendResponse(response, httpExchange);
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
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
//                    createMentor();
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

    private void createMentorProcedure(Map inputs){

        String firstName = inputs.get("fname").toString();
        String lastName = inputs.get("lname").toString();
        String password = inputs.get("password").toString();
        System.out.println(firstName + lastName + password);
        GeneralModelFactory.getByType(MentorFactoryImpl.class).create(firstName, lastName, password);
    }

//    private void createMentor(){
//
//        String firstName = view.getUserInput("Enter first name: ");
//        String lastName = view.getUserInput("Enter last name: ");
//        String password = view.getUserInput("Enter password: ");
//        Mentor mentor = GeneralModelFactory.getByType(MentorFactoryImpl.class)
//                                .create(firstName, lastName, password);
//        view.clearScreen();
//        view.displayMessageInNextLine("Mentor created: \n");
//        view.displayUserWithDetails(mentor);
//    }

    private void editMentorProcedure(Map inputs){
        String mentorId = inputs.get("fname").toString();
        Mentor mentor = SchoolController.getMentorByUserChoice(mentorId);
        System.out.println(mentor.getId());
        String editWord = inputs.get("password").toString();
        String option = inputs.get("lname").toString();
        System.out.println(option + editWord);
        switch (option) {
            case "1":
                String firstName = editWord;
                mentor.setFirstName(firstName);
                break;
            case "2":
                String lastName = editWord;
                mentor.setLastName(lastName);
                break;
            case "3":
                String password = editWord;
                mentor.setPassword(password);
                break;
            case "4":
                String email = editWord;
                mentor.setEmail(email);
                break;
            case "5":
                SchoolController.editMentorGroup(mentor, editWord);
//                SchoolController.assignMentorToGroup(mentor);
                break;
            case "0":
                break;
        }
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
