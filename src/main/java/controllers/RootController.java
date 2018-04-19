package controllers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.HttpServer;
import dao.*;
import enums.DatabaseSetup;
import enums.FilePath;
import exceptions.LoginFailure;
import factory.ConnectionFactory;
import managers.*;
import tools.Static;
import view.RootView;


public class RootController {

    private RootView view;
    private boolean shouldExit;

    public static RootController getInstance() {
        return new RootController();
    }

    private RootController() {
        view = new RootView();
        shouldExit = false;
        setDatabase();
    }

    public void runApplication(){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/static", new Static());
            server.createContext("/login", new LoginController());
            server.createContext("/", new IntroController());
            server.createContext("/admin", new AdminController());
            server.createContext("/mentor", new MentorController());
            server.createContext("/student", new StudentController());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDatabase() {
        setSqliteDatabase();
    }

    private void setSqliteDatabase() {

        SQLManager sqlManager = SqliteManager.getManager(FilePath.SQLITE_DATABASE);
        String url = DatabaseSetup.SQLITE_URL.getData();
        String driver = DatabaseSetup.SQLITE_DRIVER.getData();


        DatabaseConfig dbConfig = DatabaseConfig
                .createSQLiteConfiguration(url, driver, 2, 4);
        DatabaseConnection databaseConnection = SQLConnectionGetter
                .getSqliteConGetter(dbConfig, sqlManager, FilePath.SQL_SCRIPT);
        ConnectionFactory.setSqlConnectionGetter(databaseConnection);
    }
}