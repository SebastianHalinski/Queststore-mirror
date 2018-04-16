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
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserController loggingProcedure() {
        String login = view.getLogin();
        String password = view.getPassword();
        UserController controller = null;
        try {
            controller = SpecialDaoFactory.getByType(LoginDAO.class).getUserControllerByLoginAndPassword(login, password);
        } catch (LoginFailure ex) {
            view.clearScreen();
            view.displayMessage(ex.getMessage());
            view.handlePause();
        }
        return controller;
    }

    private void executeIntro() {
        String introFilePath = FilePath.INTRO.getPath();
        FileManager manager = new FileManagerImpl(introFilePath);
        List<String> introData = manager.getData();
        view.displayCollectionData(introData);
    }

    private void executeOutro() {
        String outroFilePath = FilePath.OUTRO.getPath();
        FileManager manager = new FileManagerImpl(outroFilePath);
        List<String> outroData = manager.getData();
        view.displayCollectionData(outroData);
        view.handlePause();
    }

    private void showAuthors() {
        String authorsFilePath = FilePath.AUTHORS.getPath();
        FileManager manager = new FileManagerImpl(authorsFilePath);
        List<String> introData = manager.getData();
        view.displayCollectionData(introData);
        view.handlePause();
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