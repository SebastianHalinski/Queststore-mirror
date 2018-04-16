package controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDAO;
import dao.SpecialDaoFactory;
import exceptions.LoginFailure;
import model.Mentor;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class LoginController implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //cookie = createSessionCookie(httpExchange);
        String response = "";
        String method = httpExchange.getRequestMethod();
        //User user = getLoggedUsserAccount(cookie);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.twig");
        JtwigModel model = JtwigModel.newModel();



        if (method.equals("GET")) {
            response = template.render(model);
            sendResponse(response, httpExchange);
        }
        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map inputs = parseFormData(formData);
            UserController controller = loggingProcedure(inputs);
            Headers headers = httpExchange.getResponseHeaders();

            if(controller.getClass() == AdminController.class) {
                headers.set("Location", "admin");
            } else if(controller.getClass() == MentorController.class) {
                headers.set("Location", "mentor");
            } else if(controller.getClass() == StudentController.class) {
                headers.set("Location", "student");
            }
            httpExchange.sendResponseHeaders(302, -1);
        }
    }

    private UserController loggingProcedure(Map inputs) {
        String login = inputs.get("uname").toString();
        String password = inputs.get("psw").toString();
        UserController controller = null;
        try {
            controller = SpecialDaoFactory.getByType(LoginDAO.class).getUserControllerByLoginAndPassword(login, password);
        } catch (LoginFailure ex) {
            System.out.println("Login failed");
        }
        return controller;
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

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String,String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
