package controllers;

import java.io.*;
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
import view.UsersView;

public class AdminController extends UserControllerImpl implements HttpHandler {

    private UsersView view;
    private String sessionId;

    public AdminController() {
        this.view = new UsersView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;
        if (httpExchange.getRequestURI().toString().contains("sessionId")) {
            sessionId = httpExchange.getRequestURI().toString();
            redirectToAdminPage(httpExchange);
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
                getPageManager(admin, uri, adminRoot, response, template, model, httpExchange);
            }

            if (method.equals("POST")) {
                response = template.render(model);
                String uri = httpExchange.getRequestURI().toString();
                String adminRoot = "/admin";
                postPageManager(uri, adminRoot, httpExchange, response, model);
            }
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
    }

    private void postPageManager(String uri, String adminRoot, HttpExchange httpExchange, String response, JtwigModel model) throws IOException {
        if (uri.startsWith(adminRoot)) {
            if (uri.startsWith("/create_mentor", adminRoot.length())) {
                response = createMentor(httpExchange);
            } else if (uri.startsWith("/edit_mentor", adminRoot.length())) {
                response = editMentor(httpExchange);
            } else if (uri.startsWith("/create_group", adminRoot.length())) {
                response = createGroup(httpExchange);
            } else if (uri.startsWith("/createexplvl", adminRoot.length())) {
                response = createExpLvl(httpExchange);
            }
            else if (uri.startsWith("/display_mentor", adminRoot.length())) {
                response = displayMentor(httpExchange);
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
        Map inputs = getInputsMap(httpExchange);
        Mentor mentor = getMentorDetails(inputs);
        if(mentor != null) {
            List<Student> students = getStudentsAssignedToMentor(mentor);
            template = JtwigTemplate.classpathTemplate("templates/admin/default_students.twig");
            model.with("studentList", students);
            model.with("groupName", mentor.getGroupName());
        } else {
            template = JtwigTemplate.classpathTemplate("templates/admin/display_students_by_mentor.twig");
            model = JtwigModel.newModel();
            List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
            model.with("mentorList", mentorList);
            model.with("errorMessage", "Cannot find mentor with given ID");
        }
        return template.render(model);
    }

    private String displayMentor(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template;
        JtwigModel model;
        Map inputs = getInputsMap(httpExchange);
        Mentor mentor = getMentorDetails(inputs);
        if(mentor != null) {
            template = JtwigTemplate.classpathTemplate("templates/admin/default_mentor.twig");
            model = JtwigModel.newModel();
            model.with("mentorName", mentor.getFullName());
            model.with("role", mentor.getRole());
            model.with("idNumber", mentor.getId());
            model.with("emailAddress", mentor.getEmail());
            model.with("groupName", mentor.getGroupName());
        } else {
            template = JtwigTemplate.classpathTemplate("templates/admin/display_mentor.twig");
            model = JtwigModel.newModel();
            List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
            model.with("mentorList", mentorList);
            model.with("errorMessage", "Cannot find mentor with given ID");
        }
        return template.render(model);
    }

    private String createExpLvl(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/create_explvl.twig");
        JtwigModel model = JtwigModel.newModel();
        Map inputs = getInputsMap(httpExchange);
        String name = inputs.get("lvlname").toString();
        Integer points = Integer.parseInt(inputs.get("points").toString());
        ExperienceLevelsController.getInstance().createExpLevels(name, points);
        model.with("operationStatus", "New experience lvl created.");
        return template.render(model);
    }

    private Map getInputsMap(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        return parseFormData(formData);
    }

    private String createGroup(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/create_group.twig");
        JtwigModel model = JtwigModel.newModel();
        Map inputs = getInputsMap(httpExchange);
        String group = inputs.get("gname").toString();
        SchoolController.createGroup(group);
        model.with("operationStatus", "New group was created.");
        return template.render(model);
    }

    private String editMentor(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/edit_mentor.twig");
        JtwigModel model = JtwigModel.newModel();
        Map inputs = getInputsMap(httpExchange);

        if(isEnteredMentorIdCorrect(inputs)) {
            editMentorProcedure(inputs);
            model.with("operationStatus", "Operation was succesfull");
        } else {
            model.with("operationStatus", "Cannot find mentor with given ID");
        }

        List<Mentor> mentorList = ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
        model.with("mentorList", mentorList);
        return template.render(model);
    }

    private boolean isEnteredMentorIdCorrect(Map inputs) {
        String mentorId = inputs.get("mentorId").toString();
        return SchoolController.getMentorByUserChoice(mentorId) != null;
    }

    private String createMentor(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/create_mentor.twig");
        JtwigModel model = JtwigModel.newModel();
        Map inputs = getInputsMap(httpExchange);
        createMentorProcedure(inputs);
        model.with("operationStatus", "New mentor created.");
        return template.render(model);
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
        String mentorId = inputs.get("mentorId").toString();
        Mentor mentor = SchoolController.getMentorByUserChoice(mentorId);
        String newData = inputs.get("newData").toString();
        String option = inputs.get("option").toString();
        switch (option) {
            case "1":
                mentor.setFirstName(newData);
                break;
            case "2":
                mentor.setLastName(newData);
                break;
            case "3":
                mentor.setPassword(newData);
                break;
            case "4":
                mentor.setEmail(newData);
                break;
            case "5":
                SchoolController.editMentorGroup(mentor, newData);
                break;
            case "0":
                break;
        }
    }

    private Mentor getMentorDetails(Map inputs){
        String mentorId = inputs.get("mentorId").toString();
        return SchoolController.getMentorByUserChoice(mentorId);

    }

    private List<Student> getStudentsAssignedToMentor(Mentor mentor){
        return SchoolController.getStudentsByGroup(mentor.getGroup());
    }
}
