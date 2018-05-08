package controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.QuestsStatus;
import model.*;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.StudentView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import static enums.QuestsStatus.WAITING_FOR_APPROVAL;

public class StudentController extends UserControllerImpl implements HttpHandler {
    private StudentView view;
    private String sessionId;

    public StudentController(){
        this.view = new StudentView();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = " ";
        if (httpExchange.getRequestURI().toString().contains("sessionId")) {
            sessionId = httpExchange.getRequestURI().toString();
            redirectToStudentPage(httpExchange);
            return;
        } else {
            Student student = getLoggedStudent(sessionId);
            student.setSessionId(sessionId);
            String method = httpExchange.getRequestMethod();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/student.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("userName", student.getFullName());

            if (method.equals("GET")) {
                response = template.render(model);
                String uri = httpExchange.getRequestURI().toString();
                String studentRoot = "/student";
                getPageManager(student, uri, studentRoot, response, template, model, httpExchange );
            }
            if (method.equals("POST")) {
                response = template.render(model);
                String uri = httpExchange.getRequestURI().toString();
                String studentRoot = "/student";
                postPageManager(student, uri, studentRoot, httpExchange, response, model);
            }
            view.sendResponse(response, httpExchange);
        }
    }

    private void redirectToStudentPage(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/student");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private Student getLoggedStudent(String sessionId) {
        String[] uriElements = sessionId.split("\\?");
        int id = Integer.parseInt(uriElements[2]);
        return ModelDaoFactory.getByType(StudentDAO.class).getModelById(id);
    }

    private void postPageManager(Student student, String uri, String studentRoot, HttpExchange httpExchange, String response, JtwigModel model) throws IOException {
        if (uri.startsWith(studentRoot)) {
            if (uri.startsWith("/shop", studentRoot.length())) {
//                createMentor(httpExchange);
//                return;
            } else if (uri.startsWith("/my_inventory", studentRoot.length())) {
//                editMentor(httpExchange);
//                return;
            } else if (uri.startsWith("/create_group", studentRoot.length())) {
//                createGroup(httpExchange);
//                return;
            } else if (uri.startsWith("/createexplvl", studentRoot.length())) {
//                createExpLvl(httpExchange);
//                return;
            }
            else if (uri.startsWith("/pick_quest", studentRoot.length())) {
                Map inputs = getInputsMap(httpExchange);
                List<Quest> quests = ModelDaoFactory.getByType(QuestDAO.class).getAllModels();
                Integer questId = 0;
                for(Object key : inputs.keySet()) {
                    questId = Integer.valueOf(String.valueOf(key));
                }
                Integer finalQuestId = questId;
                Quest pickedQuest = quests.stream()
                        .filter(q -> q.getId() == finalQuestId)
                        .findAny()
                        .orElse(null);
                student.getStudentsQuests().addItem(pickedQuest);
            }
            else if (uri.startsWith("/display_students_by_mentor", studentRoot.length())) {
//                response = displayStudentsByMentor(httpExchange, model);
            }
        }
        view.sendResponse(response, httpExchange);
    }

    private void getPageManager(Student student, String uri, String studentRoot, String response, JtwigTemplate template, JtwigModel model, HttpExchange httpExchange ) throws IOException {
        if (uri.startsWith(studentRoot)) {
            if (uri.startsWith("/shop", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/shop.twig");
                response = template.render(model);
            } else if (uri.startsWith("/student_details", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/student_details.twig");
                model.with("studentName", student.getFullName());
                model.with("roleDetail", student.getRole());
                model.with("idNumber", String.valueOf(student.getId()));
                model.with("emailAdress", student.getEmail());
                model.with("groupName", student.getGroup().getName());
                model.with("teamName", student.getTeam().getName());
                model.with("walletBalance", student.getWallet());
                model.with("levelName", student.getExperienceLevel());
                response = template.render(model);

            } else if (uri.startsWith("/my_inventory", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/my_inventory.twig");
                response = template.render(model);
            } else if (uri.startsWith("/my_team", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/my_team.twig");
                response = template.render(model);
            } else if (uri.startsWith("/team_inventory", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/team_inventory.twig");
                response = template.render(model);
            } else if (uri.startsWith("/pick_quest", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/available_quests.twig");
                List<Quest> quests = ModelDaoFactory.getByType(QuestDAO.class).getAllModels();
                model.with("questsList", quests);
                response = template.render(model);
            } else if (uri.startsWith("/active_quests", studentRoot.length())) {
                Set activeQuests = student.getStudentsQuests().getStock().entrySet();
                if(activeQuests.size() != 0) {
                    template = JtwigTemplate.classpathTemplate("templates/student/active_quests.twig");
                    model.with("questsList", activeQuests);
                }
                response = template.render(model);
            } else if (uri.startsWith("/my_attendance", studentRoot.length())) {
                template = JtwigTemplate.classpathTemplate("templates/student/attendance.twig");
                model.with("attendance", student.getAttendance().getPercentageAttendance());
                response = template.render(model);
            } else if (uri.startsWith("/student", studentRoot.length())) {
                redirectToStudentPage(httpExchange);
            } else if (uri.startsWith("/login", studentRoot.length())) {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.add("Location", "/login");
                httpExchange.sendResponseHeaders(302, -1);
                httpExchange.close();
                return;
            }
        }
        view.sendResponse(response, httpExchange);
    }

    private Map getInputsMap(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        return parseFormData(formData);
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }



//    private void showMyInventory() { view.displayInventory(student.getInventory()); }
//
//    private void showTeamInventory() { view.displayInventory(student.getTeam().getInventory()); }
//
//    private void executeShopping() {
//        Shop shop = new Shop();
//        ShopController controller = new ShopController(shop, student);
//        controller.executeShoppingMenu();
//        }
//
//    private void useArtifacts() {
//        showMyInventory();
//        if(student.getInventory().getStock().isEmpty()){
//            view.displayMessageInNextLine("- sorry, You have nothing to use!");
//        } else {
//            int id = view.getIntegerFromUser("Enter artifact id: ");
//            StudentInventory inventory = student.getInventory();
//            Set<Artifact> artifacts = inventory.getStock().keySet();
//            List<Artifact> artifactsCopy = new ArrayList<>(artifacts);
//            for (Iterator<Artifact> iterator = artifactsCopy.iterator(); iterator.hasNext();){
//                Artifact artifact = iterator.next();
//                if(id == artifact.getId() && inventory.getStock().get(artifact) == 1) {
//                    inventory.removeArtifact(artifact);
//                    view.displayMessageInNextLine("- artifact used!");
//                }
//                else if (id == artifact.getId() && id == artifact.getId()) {
//                    inventory.decreaseQuantity(artifact);
//                    view.displayMessageInNextLine("- artifact used!");
//                }
//            }
//        }
//    }
//
//    private void showStudentsFromMyTeam() {
//        List<Student> students = student.getTeam().getStudents();
//        view.displayMessageInNextLine("Your teammates:\n");
//        view.displayManyUsersWithDetails(students);
//    }
//    private void pickQuestToAchieve(){
//        StudentsQuestsController studentQuestsCtrl = new StudentsQuestsController();
//        studentQuestsCtrl.runQuestMenu(student);
//    }
//    private void showMyQuests() {
//        if(student.getStudentsQuests().isEmpty()) {
//            view.displayMessageInNextLine("- sorry, there is nothing to show!");
//        } else {
//        view.displayObject(student.getStudentsQuests());
//        }
//    }
//
//    private void showMyAttendance() {
//        view.displayMessageInNextLine("Your attendance:\n\t");
//        view.displayAttendanceWithDetails(student.getAttendance());
//    }
//
//    private void useTeamArtifacts() {
//        view.clearScreen();
//        showTeamInventory();
//        if(student.getTeam().getInventory().getStock().isEmpty()){
//            view.displayMessageInNextLine("- sorry, You have nothing to use!");
//        } else {
//            int id = view.getIntegerFromUser("Enter artifact id: ");
//            TeamInventory inventory = student.getTeam().getInventory();
//            Set<Artifact> artifacts = inventory.getStock().keySet();
//            List<Artifact> artifactsCopy = new ArrayList<>(artifacts);
//            for (Iterator<Artifact> iterator = artifactsCopy.iterator(); iterator.hasNext();){
//                Artifact artifact = iterator.next();
//                if(id == artifact.getId() && inventory.getStock().get(artifact) == 1) {
//                    inventory.removeArtifact(artifact);
//                    view.displayMessageInNextLine("- artifact used!");
//                    break;
//                }
//                else if ((id == artifact.getId())) {
//                    inventory.decreaseQuantity(artifact);
//                    view.displayMessageInNextLine("- artifact used!");
//                }
//            }
//        }
//    }
//
//    private void markQuest(){
//        showMyQuests();
//        List<Quest> quests = new ArrayList<>(student.getStudentsQuests().getStock().keySet());
//        int questId = view.getNotNegativeNumberFromUser("Choose quest: ");
//        for (Quest quest : quests){
//            if (quest.getId() == questId){
//                quest.setStatus(QuestsStatus.WAITING_FOR_APPROVAL.getName());
//                view.displayMessage("Actual quest status: " + quest.getStatus());
//            }
//        }
//    }
}
