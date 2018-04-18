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

    private AdminView view;
    private Admin admin;

    public AdminController() {
        this.view = new AdminView();
        admin = null;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (admin == null) {
            admin = getLoggedAdmin(httpExchange);
        } else {
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
                    } else if (uri.startsWith("/admin_details", adminRoot.length())) {
                        template = JtwigTemplate.classpathTemplate("templates/admin/admin_details.twig");
                        model.with("adminName", admin.getFullName());
                        model.with("roleDetail", admin.getRole());
                        model.with("idNumber", String.valueOf(admin.getId()));
                        model.with("emailAdress", admin.getEmail());
                        response = template.render(model);
                    } else if (uri.startsWith("/admin", adminRoot.length())) {
                        Headers responseHeaders = httpExchange.getResponseHeaders();
                        responseHeaders.add("Location", "/admin");
                        httpExchange.sendResponseHeaders(302, -1);
                        httpExchange.close();
                        return;
                    } else if (uri.startsWith("/login", adminRoot.length())) {
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
                            //Edit mentor
                        }
                    }
                }
                view.sendResponse(response, httpExchange);
            }
        }

    private Admin getLoggedAdmin(HttpExchange httpExchange) throws IOException {
        String adminRoot = "/admin?";
        String uri = httpExchange.getRequestURI().toString();
        int id = Integer.parseInt(uri.substring(adminRoot.length(), uri.length()));
        Headers requestHeaders = httpExchange.getResponseHeaders();
        requestHeaders.add("Location", "/admin");
        httpExchange.sendResponseHeaders(302,-1);
        httpExchange.close();
        return ModelDaoFactory.getByType(AdminDAO.class).getModelById(id);
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
        System.out.println(firstName + lastName + password);
        GeneralModelFactory.getByType(MentorFactoryImpl.class).create(firstName, lastName, password);
    }

    private void runExpLevelManager(){
        ExperienceLevelsController.getInstance().manageExperienceLevels();
    }
}
