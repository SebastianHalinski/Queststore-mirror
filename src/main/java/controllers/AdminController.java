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
                getPageManager(admin, uri, adminRoot, response, template, model, httpExchange );
            }
            if (method.equals("POST")) {
                response = template.render(model);
                String uri = httpExchange.getRequestURI().toString();
                String adminRoot = "/admin";
                postPageManager(uri, adminRoot, httpExchange, response, model);
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
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
        return;
    }

    private void postPageManager(String uri, String adminRoot, HttpExchange httpExchange, String response, JtwigModel model) throws IOException {
        if (uri.startsWith(adminRoot)) {
            if (uri.startsWith("/create_mentor", adminRoot.length())) {
                createMentor(httpExchange);
                return;
            } else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
                editMentor(httpExchange);
                return;
            } else if (uri.startsWith("/create_group", adminRoot.length())) {
                createGroup(httpExchange);
                return;
            } else if (uri.startsWith("/createexplvl", adminRoot.length())) {
                createExpLvl(httpExchange);
                return;
            }
            else if (uri.startsWith("/display_mentor", adminRoot.length())) {
                response = dispalyMentor(httpExchange);
            }

            else if (uri.startsWith("/display_students_by_mentor", adminRoot.length())) {
                response = displayStudentsByMentor(httpExchange, model);

            }
        }
        view.sendResponse(response, httpExchange);
    }


    private void getPageManager(Admin admin, String uri, String adminRoot, String response, JtwigTemplate template, JtwigModel model, HttpExchange httpExchange ) throws IOException {
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
                List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
                model.with("mentorList", mentorList);
                response = template.render(model);

            } else if (uri.startsWith("/display_mentor", adminRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/admin/display_mentor.twig");
                List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
                model.with("mentorList", mentorList);
                response = template.render(model);

            } else if (uri.startsWith("/display_students_by_mentor", adminRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/admin/display_students_by_mentor.twig");
                List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
                model.with("mentorList", mentorList);
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
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.add("Location", "/login");
                httpExchange.sendResponseHeaders(302, -1);
                httpExchange.close();
                return;
            }
        }
        view.sendResponse(response, httpExchange);
    }

    private String displayStudentsByMentor(HttpExchange httpExchange, JtwigModel model) throws IOException {
        JtwigTemplate template;
        String response;
        Map inputs = getInputsMap(httpExchange);
        Mentor mentor = displayMentorProcedure(inputs);
        List<Student> students = displayStudentsByMentor(mentor);
        template = JtwigTemplate.classpathTemplate("templates/admin/default_students.twig");
        model.with("studentList", students);
        model.with("groupName", mentor.getGroupName());
        response = template.render(model);
        return response;
    }

    private String dispalyMentor(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template;
        JtwigModel model;
        String response;
        Map inputs = getInputsMap(httpExchange);
        Mentor mentor = displayMentorProcedure(inputs);
        template = JtwigTemplate.classpathTemplate("templates/admin/default_mentor.twig");
        model = JtwigModel.newModel();
        model.with("mentorName", mentor.getFullName());
        model.with("role", mentor.getRole());
        model.with("idNumber", mentor.getId());
        model.with("emailAddress", mentor.getEmail());
        model.with("groupName", mentor.getGroupName());
        response = template.render(model);
        return response;
    }

    private void createExpLvl(HttpExchange httpExchange) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        String name = inputs.get("fname").toString();
        Integer points = Integer.parseInt(inputs.get("points").toString());
        ExperienceLevelsController.getInstance().createExpLevels(name, points);
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private Map getInputsMap(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        return parseFormData(formData);
    }

    private void createGroup(HttpExchange httpExchange) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        String group = inputs.get("fname").toString();
        SchoolController.createGroup(group);
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private void editMentor(HttpExchange httpExchange) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        editMentorProcedure(inputs);
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private void createMentor(HttpExchange httpExchange) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        createMentorProcedure(inputs);
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302, -1);
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
                break;
            case "0":
                break;
        }
    }


    private Mentor displayMentorProcedure(Map inputs){
        String mentorId = inputs.get("mentorId").toString();
        return SchoolController.getMentorByUserChoice(mentorId);

    }

    private List<Student> displayStudentsByMentor(Mentor mentor){
        return SchoolController.getStudentsByGroup(mentor.getGroup());
    }

}
