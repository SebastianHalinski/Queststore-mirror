package controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDAO;
import dao.SpecialDaoFactory;
import exceptions.LoginFailure;
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

    LoginController() {
        view = new UsersView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = null;
        HttpCookie cookie = createSessionCookie(httpExchange);
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login.twig");
        JtwigModel model = JtwigModel.newModel();

        if (method.equals("GET")) {
            String response = template.render(model);
            view.sendResponse(response, httpExchange);
            }

        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map inputs = parseFormData(formData);
            user = loggingProcedure(inputs,user, cookie);
            Headers headers = httpExchange.getResponseHeaders();

            if(user.getRole().equals("admin")) {
                headers.set("Location", "admin?" + cookie.toString() +"?" +user.getId());
            } else if(user.getRole().equals("mentor")) {
                headers.set("Location", "mentor");
            } else if(user.getRole().equals("student")) {
                headers.set("Location", "student");
            }
            user = null;
            httpExchange.sendResponseHeaders(302, -1);
        }
    }

    private User loggingProcedure(Map inputs, User user, HttpCookie cookie) {
        String login = inputs.get("uname").toString();
        String password = inputs.get("psw").toString();
        try {
            user = SpecialDaoFactory.getByType(LoginDAO.class).getUserByLoginAndPassword(login, password);
            user.setSessionId(cookie.toString());
        } catch (LoginFailure ex) {
            ex.getMessage();
        }
        return user;
    }

    private HttpCookie createSessionCookie(HttpExchange httpExchange) {
        HttpCookie cookie = new HttpCookie("sessionId", UUID.randomUUID().toString());
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
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
