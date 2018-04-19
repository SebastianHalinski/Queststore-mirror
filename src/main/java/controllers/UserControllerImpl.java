package controllers;

import java.util.List;

import model.ModelDaoFactory;
import model.User;
import model.ArtifactDAO;
import model.Artifact;
import model.QuestDAO;
import model.Quest;
import view.UsersView;

public abstract class  UserControllerImpl implements UserController {


    protected <T extends User> void showProfile(T user){
        UsersView view = new UsersView();
        view.displayUserWithDetails(user);
    }

    protected List<Artifact> getArtifacts() {
        return ModelDaoFactory.getByType(ArtifactDAO.class).getAllModels();
    }

    protected List<Quest> getQuests() {
        return ModelDaoFactory.getByType(QuestDAO.class).getAllModels();
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