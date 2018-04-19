package controllers;

import java.io.*;
import java.net.HttpCookie;
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

    private AdminView view;
    private String sessionId;

    public AdminController() {
        this.view = new AdminView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = " ";
        if (httpExchange.getRequestURI().toString().contains("sessionId")) {
            sessionId = httpExchange.getRequestURI().toString();
            redirectToAdminPage(httpExchange);
            return;
        } else {
            Admin admin = getLoggedAdmin(sessionId);
            admin.setSessionId(sessionId);
            String method = httpExchange.getRequestMethod();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/admin.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("userName", admin.getFullName());

            if (method.equals("GET")) {
                response = template.render(model);
                String uri = httpExchange.getRequestURI().toString();
                String adminRoot = "/admin";

                if (uri.startsWith(adminRoot)) {
                    if (uri.startsWith("/create_mentor", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/create_mentor.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/admin_details", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/admin_details.twig");
                        model.with("adminName", admin.getFullName());
                        model.with("roleDetail", admin.getRole());
                        model.with("idNumber", String.valueOf(admin.getId()));
                        model.with("emailAdress", admin.getEmail());
                        response = template.render(model);
                    } else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/edit_mentor.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/display_mentor", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/display_mentor.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/display_students_by_mentor", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/display_students_by_mentor.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/create_group", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/create_group.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/createexplvl", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/create_explvl.twig");
                        response = template.render(model);
                    } else if (uri.startsWith("/admin", adminRoot.length())) {
                        redirectToAdminPage(httpExchange);
                    } else if (uri.startsWith("/login", adminRoot.length())) {
                        admin = null;
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
                if (uri.startsWith(adminRoot)) {
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
                    } else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
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
                    } else if (uri.startsWith("/create_group", adminRoot.length())) {
                        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String formData = br.readLine();
                        Map inputs = parseFormData(formData);
                        String group = inputs.get("fname").toString();
                        SchoolController.createGroup(group);
                        Headers responseHeaders = httpExchange.getResponseHeaders();
                        responseHeaders.add("Location", "/admin");
                        httpExchange.sendResponseHeaders(302, -1);
                        httpExchange.close();
                        return;
                    } else if (uri.startsWith("/createexplvl", adminRoot.length())) {
                        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String formData = br.readLine();
                        Map inputs = parseFormData(formData);
                        String name = inputs.get("fname").toString();
                        Integer points = Integer.parseInt(inputs.get("points").toString());
                        ExperienceLevelsController.getInstance().createExpLevels(name, points);
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
    }

    private Admin getLoggedAdmin(String sessionId) {
        String[] uriElements = sessionId.split("\\?");
        int id = Integer.parseInt(uriElements[2]);
        return ModelDaoFactory.getByType(AdminDAO.class).getModelById(id);
    }

    private void redirectToAdminPage(HttpExchange httpExchange) throws IOException {
        Headers requestHeaders = httpExchange.getResponseHeaders();
        requestHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302,-1);
        httpExchange.close();
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

    private void createMentorProcedure(Map inputs){
        String firstName = inputs.get("fname").toString();
        String lastName = inputs.get("lname").toString();
        String password = inputs.get("password").toString();
        GeneralModelFactory.getByType(MentorFactoryImpl.class).create(firstName, lastName, password);
    }

    private void editMentorProcedure(Map inputs){
        String mentorId = inputs.get("fname").toString();
        Mentor mentor = SchoolController.getMentorByUserChoice(mentorId);
        String editWord = inputs.get("password").toString();
        String option = inputs.get("lname").toString();
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
