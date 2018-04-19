package controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDAO;
import dao.SpecialDaoFactory;
import exceptions.LoginFailure;
import model.Mentor;
import model.User;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.UsersView;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.*;

public class LoginController implements HttpHandler  {
    private UsersView view;
    private List<User> loggedUsers = new ArrayList<>();
    private HttpCookie cookie;

    LoginController() {
        view = new UsersView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        cookie = createSessionCookie(httpExchange);
        User loggedUser = getLoggedUsserAccount(cookie);
        String response;
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.twig");
        JtwigModel model = JtwigModel.newModel();

        if (method.equals("GET")) {
            response = template.render(model);
            view.sendResponse(response, httpExchange);
            }

        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map inputs = parseFormData(formData);
            loggedUser = loggingProcedure(inputs, loggedUser);
            Headers headers = httpExchange.getResponseHeaders();

            if(loggedUser.getRole().equals("admin")) {
                headers.set("Location", "admin?"+ loggedUser.getId());
            } else if(loggedUser.getRole().equals("mentor")) {
                headers.set("Location", "mentor");
            } else if(loggedUser.getRole().equals("student")) {
                headers.set("Location", "student");
            }
            httpExchange.sendResponseHeaders(302, -1);
        }
    }

    private User loggingProcedure(Map inputs, User user) {
        String login = inputs.get("uname").toString();
        String password = inputs.get("psw").toString();

        try {
            user = SpecialDaoFactory.getByType(LoginDAO.class).getUserByLoginAndPassword(login, password);
            loggedUsers.add(user);
        } catch (LoginFailure ex) {
            System.out.println("Login failed");
        }
        return user;
    }

    private User getLoggedUsserAccount(HttpCookie cookie) {
        for(User user : loggedUsers) {
            if(cookie.toString().equals(user.getSessionId())){
                return user;
            }
        }
        return null;
    }

    private HttpCookie createSessionCookie(HttpExchange httpExchange) {
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        HttpCookie cookie;
        if (cookieStr != null) {  // Cookie already exists
            cookie = HttpCookie.parse(cookieStr).get(0);
        }
        else {
            cookie = new HttpCookie("sessionId", UUID.randomUUID().toString());
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
        }
        return cookie;
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
