package controllers;

import dao.SchoolDAO;
import dao.SpecialDaoFactory;
import factory.GeneralModelFactory;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class SchoolController {

    public static List<Group> getGroups() {
        return ModelDaoFactory.getByType(GroupDAO.class).getAllModels();
    }

    public static List<Team> getTeams() {
        return ModelDaoFactory.getByType(TeamDAO.class).getAllModels();
    }

    public static List<String> getGroupNames() {
        return SpecialDaoFactory.getByType(SchoolDAO.class).getGroupNames();
    }

    public static List<String> getTeamNames() {
        return SpecialDaoFactory.getByType(SchoolDAO.class).getTeamNames();
    }

    public static void editMentorGroup(Mentor mentor, String group){
        Group newGroup = GeneralModelFactory.getByType(GroupFactoryImpl.class).create(group);
        mentor.setGroup(newGroup);
    }

    public static void assignStudentToGroup(Student student){
        //SchoolView view = new SchoolView();
        List<Group> groups = getGroups();
        boolean isStudentAssigned = false;
        String chosenGroupName = "";
        while (!isStudentAssigned && !chosenGroupName.equals("0")){
            //view.displayObjects(groups);
            //chosenGroupName = view.getUserInput("Choose group by name (or type 0 to exit): ");
            for (Group group : groups){
                if (chosenGroupName.equals(group.getName())){
                    student.setGroup(group);
                    //view.displayMessageInNextLine("- student moved to group: " + group.getName());
                    if (student.getTeam().getId() != 1){
                        student.setTeam(getDefaultTeam());
                        //view.displayMessageInNextLine("- student moved to undefined team...");
                    }
                    isStudentAssigned = true;
                    break;
                }
            }
            if (!isStudentAssigned && !chosenGroupName.equals("0")){
                //view.displayMessageInNextLine("- there is no such group...");
            }
        }
    }

    public static void assignStudentToTeam(Student student) {
        //SchoolView view = new SchoolView();
        List<Team> teams = getTeamsByGroup(student.getGroup());
        boolean isStudentAssigned = false;
        String chosenTeamName = "";
        while (!isStudentAssigned && !chosenTeamName.equals("0")){
            //view.displayObjects(teams);
            //chosenTeamName = view.getUserInput("Choose team by name (or type 0 to exit): ");
            for (Team team : teams){
                if (chosenTeamName.equals(team.getName())){
                    student.setTeam(team);
                    //view.displayMessageInNextLine("- student moved to team: " + team.getName());
                    isStudentAssigned = true;
                    break;
                }
            }
            if (!isStudentAssigned && !chosenTeamName.equals("0")){
                //view.displayMessageInNextLine("- there is no such group...");
            }
        }
    }

    public static List<Team> getTeamsByGroup(Group group){
        List<Team> teams = getTeams();
        List<Team> teamsByGroup = new ArrayList<>();
        for (Team team : teams){
            team.setStudents();
            if (team.size() == 0){
                teamsByGroup.add(team);
            } else {
                int groupId = team.getStudents().get(0).getGroup().getId();
                if (groupId == group.getId()){
                    teamsByGroup.add(team);
                }
            }
        }
        return teamsByGroup;
    }

    public static List<Student> getStudentsByGroup(Group group) {
        return group.getStudents();
    }

    public static List<Student> getStudentsByTeam(Team team) {
        return team.getStudents();
    }

    public static List<Student> getAllStudents() {
        return ModelDaoFactory.getByType(StudentDAO.class).getAllModels();
    }

    public static List<Mentor> getAllMentors() {
        return ModelDaoFactory.getByType(MentorDAO.class).getAllModels();
    }

    public static Student pickStudentFromList(List<Student> students) {
        //SchoolView view = new SchoolView();
        //view.displayObjects(students);
        //String id = view.getUserInput("Choose student by id: ");
        //view.drawNextLine();
        return students.stream()
                //.filter(s -> String.valueOf(s.getId()).equals(id))
                .findAny()
                .orElse(null);
    }

    private static Team getDefaultTeam(){
        return GeneralModelFactory.getByType(TeamFactoryImpl.class).getDefault();
    }

    public static Mentor getMentorByUserChoice(String mentorId) {
        List<Mentor> mentors = getAllMentors();
        return mentors.stream()
                .filter(m -> String.valueOf(m.getId()).equals(mentorId))
                .findAny()
                .orElse(null);
    }

//    public static void createNewTeam(){
//        boolean isDone = false;
//        String teamName;
//        //SchoolView view = new SchoolView();
//        while (!isDone){
//            //view.clearScreen();
//            //teamName = view.getUserInput("Enter team name (or 0 to exit): ");
//            if (teamName.equals("0")){
//                isDone = true;
//            } else if (getTeamNames().contains(teamName)) {
//                //view.displayMessageInNextLine("- Team already exist...");
//            } else {
//                Team team = GeneralModelFactory.getByType(TeamFactoryImpl.class)
//                        .create(teamName);
//
//                //view.clearScreen();
//                //view.displayMessageInNextLine("- Team created: \n");
//                //view.displayObject(team);
//                isDone = true;
//            }
//            if(! isDone) {
//                //view.handlePause();
//            }
//        }
//    }
    public static void createGroup(String group){
        GeneralModelFactory.getByType(GroupFactoryImpl.class).create(group);
    }

    public static void checkAttendance(Mentor mentor){
        //SchoolView view = new SchoolView();
        for (Student student : getStudentsByGroup(mentor.getGroup())){
            boolean isPresenceChecked = false;
            while (!isPresenceChecked){
                //view.clearScreen();
                //String userInput = view.getUserInput(String.format("- Is %s present (y/anything else): ", student.getFullName()));
                //boolean isPresent = userInput.equals("y");
                //student.addAttendance(isPresent);
                isPresenceChecked = true;
            }
        }
    }
}
