package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.QuestsStatus;
import factory.GeneralModelFactory;
import model.ArtifactFactoryImpl;
import model.QuestFactoryImpl;
import model.StudentFactoryImpl;
import model.Mentor;
import model.Student;
import model.Artifact;
import model.Quest;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.MentorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MentorController extends UserControllerImpl implements HttpHandler {
    private MentorView view;
    private Mentor mentor;

    public MentorController(){
        this.mentor = mentor;
        view = new MentorView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = " ";
        String method = httpExchange.getRequestMethod();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor/mentor.twig");
        JtwigModel model = JtwigModel.newModel();

        if (method.equals("GET")) {
            response = template.render(model);
        }
        if (method.equals("POST")) {
            //actions
        }
        view.sendResponse(response, httpExchange);
    }

    public void executeMainMenu() {
        boolean isDone = false;
        while(! isDone){
            String[] correctChoices = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "0"};
            view.clearScreen();
            view.displayMenu();
            String userChoice = view.getMenuChoice(correctChoices);
            view.clearScreen();
            switch(userChoice){
                case "1":
                    showProfile(mentor);
                    break;
                case "2":
                    createStudent();
                    break;
                case "3":
                    assignStudentToGroup();
                    break;
                case "4":
                    assignStudentToTeam();
                    break;
                case "5":
                    createQuest();
                    break;
                case "6":
                    editQuest();
                    break;
                case "7":
                    createArtifact();
                    break;
                case "8":
                    editArtifact();
                    break;
                case "9":
                    markStudentQuest();
                    break;
                case "10":
                    displayStudentDetails();
                    break;
                case "11":
                    markStudentArtifacts();
                    break;
                case "12":
                    showStudentsFromMyGroup();
                    break;
                case "13":
                    SchoolController.createNewTeam();
                    break;
                case "14":
                    SchoolController.checkAttendance(mentor);
                    break;
                case "0":
                    isDone = true;
                    break;
            }
            view.handlePause();
        }
    }

    private void createQuest() {
        String name = view.getUserInput("Enter quest name: ");
        String description = view.getUserInput("Enter quest description: ");
        int reward = view.getItemValue();
        Quest quest = GeneralModelFactory.getByType(QuestFactoryImpl.class)
                                .create(name, description, reward);

        view.clearScreen();
        view.displayMessage("You've created something new:\n");
        view.displayItemWithDetails(quest);
    }

    private void editQuest() {
        List<Quest> quests = getQuests();
        view.displayElementsOfCollection(prepareObjectsToDisplay(quests));
        String input = view.getUserInput("Enter id of quest which you would like to edit: ");
        for(Quest quest : quests) {
            if(String.valueOf(quest.getId()).equals(input)) {
                executeQuestEditMenu(quest);
                break;
            }
        }

    }

    private void createStudent(){
        String firstName = view.getUserInput("Enter first name: ");
        String lastName = view.getUserInput("Enter last name: ");
        String password = view.getUserInput("Enter password: ");
        Student student = GeneralModelFactory.getByType(StudentFactoryImpl.class)
                                    .create(firstName, lastName, password);

        view.displayMessageInNextLine("Student created successfully! \n");
        view.displayUserWithDetails(student);
    }

    private void createArtifact(){
        String name = view.getUserInput("Enter artifact name: ");
        String description = view.getUserInput("Enter artifact description: ");
        int price = view.getItemValue();
        Artifact artifact = GeneralModelFactory.getByType(ArtifactFactoryImpl.class)
                                        .create(name, description, price);

        view.clearScreen();
        view.displayMessageInNextLine("You've created something new:\n");
        view.displayItemWithDetails(artifact);
        }

    private void editArtifact(){
        List<Artifact> artifacts = getArtifacts();
        view.displayElementsOfCollection(prepareObjectsToDisplay(artifacts));
        String input = view.getUserInput("Enter id of artifact which you would like to edit: ");
        for(Artifact artifact : artifacts) {
            if(String.valueOf(artifact.getId()).equals(input)) {
                executeArtifactEditMenu(artifact);
                break;
            }
        }
    }

    private void executeArtifactEditMenu(Artifact artifact){
        boolean isDone = false;
        while(! isDone){
            String[] correctChoices = {"1", "2", "3", "4", "0"};
            view.clearScreen();
            view.displayMessage("Artifact to edit:\n");
            view.displayItemWithDetails(artifact);
            view.drawNextLine();
            view.displayArtifactMenu();
            String userChoice = view.getMenuChoice(correctChoices);
            switch(userChoice) {
                case "1":
                    artifact.setName(view.getUserInput("Enter new artifact name: "));
                    break;
                case "2":
                    artifact.setType(view.getUserInput("Enter new artifact type: ").charAt(0));
                    break;
                case "3":
                    artifact.setDescription(view.getUserInput("Enter new artifact description: "));
                    break;
                case "4":
                    artifact.setPrice(view.getItemValue());
                    break;
                case "0":
                    isDone = true;
                    break;
            }
        }
        view.clearScreen();
        view.displayMessageInNextLine("Artifact after changes:\n");
        view.displayItemWithDetails(artifact);
    }

    private void executeQuestEditMenu(Quest quest){
        boolean isDone = false;
        while(! isDone) {
            String[] correctChoices = {"1", "2", "3", "4", "0"};
            view.clearScreen();
            view.displayMessage("Quest to edit:\n");
            view.displayItemWithDetails(quest);
            view.drawNextLine();
            view.displayQuestMenu();
            String userChoice = view.getMenuChoice(correctChoices);
            switch (userChoice) {
                case "1":
                    quest.setName(view.getUserInput("Enter new quest name: "));
                    break;
                case "2":
                    quest.setType(view.getUserInput("Enter new quest type: ").charAt(0));
                    break;
                case "3":
                    quest.setDescription(view.getUserInput("Enter new quest description: "));
                    break;
                case "4":
                    quest.setReward(view.getItemValue());
                    break;
                case "0":
                    isDone = true;
                    break;
            }
        }
        view.clearScreen();
        view.displayMessageInNextLine("Quest after changes:\n");
        view.displayItemWithDetails(quest);
    }

    private void markStudentQuest() {
        List<Student> students = SchoolController.getStudentsByGroup(mentor.getGroup());
        Student student = SchoolController.pickStudentFromList(students);
        List<Quest> studentQuests = new ArrayList<>(student.getStudentsQuests().getStock().keySet());
        view.displayObjects(studentQuests);
        int questId = view.getNotNegativeNumberFromUser("Choose quest: ");
        for (Quest quest : studentQuests){
            if (quest.getId() == questId){
                int questStatus = view.getNotNegativeNumberFromUser("1. Complete\n" +
                                                                    "    2. To improve\n" +
                                                                    "    Choose: ");
                switch (questStatus){
                    case 1:
                        quest.setStatus(QuestsStatus.COMPLETED.getName());
                        break;
                    case 2:
                        quest.setStatus(QuestsStatus.TO_IMPROVE.getName());
                        break;
                }
                view.displayMessage("Actual quest status: " + quest.getStatus());
            }
        }
    }

    private void markStudentArtifacts() {
        executeNotImplementedInfo();
    }

    private void showStudentsFromMyGroup() {
        List<Student> students = SchoolController.getStudentsByGroup(mentor.getGroup());
        view.displayMessageInNextLine("Your students:\n");
        view.displayManyUsersWithDetails(students);
    }

    private void assignStudentToGroup() {
        List<Student> students = SchoolController.getAllStudents();
        Student student = SchoolController.pickStudentFromList(students);
        if (student != null){
            SchoolController.assignStudentToGroup(student);
            view.clearScreen();
            view.displayMessageInNextLine("Student edited:\n");
            view.displayUserWithDetails(student);
        } else {
            view.displayMessageInNextLine("Student does not exist...");
        }
    }

    private void assignStudentToTeam() {
        List<Student> students = SchoolController.getStudentsByGroup(mentor.getGroup());
        Student student = SchoolController.pickStudentFromList(students);
        if (student != null){
            SchoolController.assignStudentToTeam(student);
            view.clearScreen();
            view.displayMessageInNextLine("Student edited:\n");
            view.displayUserWithDetails(student);
        } else {
            view.displayMessageInNextLine("Student does not exist...");
        }
    }

    private void displayStudentDetails() {
        Student student = SchoolController.pickStudentFromList(mentor.getGroup().getStudents());
        if (student != null) {
            view.clearScreen();
            view.displayMessageInNextLine("Detailed info:\n");
            view.displayUserWithDetails(student);
            String attendanceInfo = student.getAttendance().getFullDataToString();
            view.displayMessage(attendanceInfo);
        } else {
            view.displayMessage("- there is no one to show..");
        }
    }
}
