package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class LoginController implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //cookie = createSessionCookie(httpExchange);
        String response = "";
        String method = httpExchange.getRequestMethod();
        //User user = getLoggedUsserAccount(cookie);
        if (method.equals("GET PIWO")) {
            response = "PIWO";
        }
        if (method.equals("POST")) {
            response = "POST PIWO";
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
}
