package controllers;


import model.ExpLevelsFactoryImpl;
import model.ExperienceLevels;
import tools.DataTool;

import model.Student;
import factory.GeneralModelFactory;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ExperienceLevelsController {

    private ExperienceLevels experienceLevels;


    public static ExperienceLevelsController getInstance() {
        return new ExperienceLevelsController();
    }

    private ExperienceLevelsController() {
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


}

