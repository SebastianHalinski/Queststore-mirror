package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.UsersView;

import java.io.IOException;
import java.io.OutputStream;

public class IntroController implements HttpHandler {

    private UsersView view;

     IntroController() {
        this.view = new UsersView();
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/intro.twig");
        JtwigModel model = JtwigModel.newModel();
        if (method.equals("GET")) {
            response = template.render(model);
        }
        view.sendResponse(response, httpExchange);
    }
}
