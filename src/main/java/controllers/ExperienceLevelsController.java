package controllers;

import factory.ConnectionFactory;
import managers.SQLManager;
import managers.SqliteManager;
import model.ExpLevelsFactoryImpl;
import model.ExperienceLevels;
import tools.DataTool;
import enums.FilePath;
import model.Student;
import view.SchoolView;
import factory.GeneralModelFactory;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ExperienceLevelsController {

    private SchoolView view;
    private ExperienceLevels experienceLevels;
    private boolean shouldExit = false;

    public static ExperienceLevelsController getInstance() {
        return new ExperienceLevelsController();
    }

    private ExperienceLevelsController() {
        view = new SchoolView();
        experienceLevels = getExperienceLevels();
    }

    public ExperienceLevels getExperienceLevels() {
        if (experienceLevels == null){
            return GeneralModelFactory.getByType(ExpLevelsFactoryImpl.class).create();
        }
        return experienceLevels;
    }

    public void setStudentExperienceLevel(Student student) {
        Map<String,Integer> levels = experienceLevels.getUpdatedLevels();
        List<Integer> expValues = new ArrayList<>(levels.values());
        Collections.sort(expValues);
        int studentExperience = student.getExperience();
        int index = 0;
        int currentExpLevel = 0;
        int nextExpLevel = studentExperience;  // if the student has exceeded the maximum level
        for(int value : expValues) {
            if(value > studentExperience) {
                break;
            }
            index++;
        }
        if(index > 0) {
            currentExpLevel = expValues.get(index-1);
        }
        if(index < expValues.size()) {  // protection with a single element list
            nextExpLevel = expValues.get(index);
        }
        String level = DataTool.getKeyByValue(levels, currentExpLevel);
        student.setExperienceLevel(level, nextExpLevel);
    }

    public void createExpLevels(String name, Integer points){
        experienceLevels.addLevel(name, points);
    }

    public void manageExperienceLevels() {
        while(! shouldExit) {
            view.clearScreen();
            view.displayExpLevelsManager();
            String[] correctChoices = {"1", "2", "3", "4", "5", "0"};
            String userChoice = view.getMenuChoice(correctChoices);
            switch (userChoice) {
                case "1":
                    showExperienceLevels();
                    break;
                case "2":
                    restoreDefaultExpLevels();
                    break;
                case "3":
                    modifyExperienceLevels();
                    break;
                case "4":
                    clearChosenExperienceLevel();
                    break;
                case "5":
                    clearAllExperienceLevels();
                    break;
                case "0":
                    shouldExit = true;
                    break;
            }
            if(! shouldExit){
                view.handlePause();
            }
        }
        shouldExit = false;
    }

    private void showExperienceLevels() {
        view.displayObject(getExperienceLevels());
    }

    private void restoreDefaultExpLevels() {
        resetExpLevelsFromSqlFile();
        view.clearScreen();
        view.displayMessageInNextLine("Restored:");
        showExperienceLevels();
    }

    private void modifyExperienceLevels() {
        experienceLevels = getExperienceLevels();
        String levelName;
        view.clearScreen();
        view.displayMessage("Current state:");
        view.displayObject(experienceLevels);
        levelName = view.getUserInput(" Type:\n\n\t- new level name to add level\n" +
                "\t- or existing level name to modify level\n" +
                "\t- or press '0' to quit process ---> ");
        if(! levelName.equals("0")) {
            int levelValue = 0;
            boolean shouldContinue = true;
            while(shouldContinue) {
                levelValue = view.getNotNegativeNumberFromUser("- type required experience level ---> ");
                shouldContinue = experienceLevels.containsGivenValue(levelValue);
                if(shouldContinue){
                    view.displayMessage("- Chosen value already exists! Type new value..");
                    view.handlePause();
                }
            }
            experienceLevels.addLevel(levelName, levelValue);
            view.clearScreen();
            view.displayMessageInNextLine("Added:");
            showExperienceLevels();
        }
    }

    private void clearAllExperienceLevels() {
        experienceLevels = getExperienceLevels();
        experienceLevels.clearLevels();
        view.clearScreen();
        view.displayMessageInNextLine("Cleared:");
        showExperienceLevels();
    }

    private void clearChosenExperienceLevel() {
        experienceLevels = getExperienceLevels();
        view.clearScreen();
        boolean isDone = false;
        String levelName = "";
        while(! isDone && ! levelName.equals("0")){
            view.clearScreen();
            view.displayMessage("Current state:");
            view.displayObject(experienceLevels);
            levelName = view.getUserInput("Type:\n\n" +
                    "\t- existing level name to modify level\n" +
                    "\t- or press '0' to quit process ---> ");
            isDone = experienceLevels.containsGivenLevel(levelName);
            if(! isDone && ! levelName.equals("0")){
                view.displayMessage("- You should type existing level name.");
            }
        }
        if (! levelName.equals("0")) {
            view.clearScreen();
            experienceLevels.removeLevel(levelName);
            view.displayMessageInNextLine("Cleared:");
            showExperienceLevels();
        }
    }

    private void resetExpLevelsFromSqlFile() {
        try {
            Connection connection = ConnectionFactory.getConnection();
            SQLManager sqlManager = SqliteManager.getManager(FilePath.SQLITE_DATABASE);
            sqlManager.updateDatabaseWithSqlFile(FilePath.UPDATE_EXP_LVL, connection);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

