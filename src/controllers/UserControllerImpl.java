package controllers;

import java.util.List;

import dao.DaoFactory;
import model.User;
import dao.ArtifactDAO;
import model.Artifact;
import dao.QuestDAO;
import model.Quest;
import view.UsersView;

public abstract class  UserControllerImpl implements UserController {

    public abstract void executeMainMenu();

    protected <T extends User> void showProfile(T user){
        UsersView view = new UsersView();
        view.displayUserWithDetails(user);
    }

    protected List<Artifact> getArtifacts() {
        return DaoFactory.getByType(ArtifactDAO.class).getAllModels();
    }

    protected List<Quest> getQuests() {
        return DaoFactory.getByType(QuestDAO.class).getAllModels();
    }

    protected void executeNotImplementedInfo() {
        UsersView view = new UsersView();
        view.displayMessage("Not implemented yet");
    }

    protected <T> String[] prepareObjectsToDisplay(List<T> objects) {
        String[] objectsToDisplay = new String[objects.size()];
        int index = 0;
        for(T object : objects){
            objectsToDisplay[index] = object.toString();
            index ++;
        }
        return objectsToDisplay;
    }
}