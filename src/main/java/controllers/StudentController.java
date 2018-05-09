package controllers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.QuestsStatus;
import model.*;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import view.StudentView;


import javax.swing.*;
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
                response = handleUsingInventoryArtifactByStudent(httpExchange, student);
            } else if (uri.startsWith("/create_group", studentRoot.length())) {
//                createGroup(httpExchange);
//                return;
            } else if (uri.startsWith("/createexplvl", studentRoot.length())) {
//                createExpLvl(httpExchange);
//                return;
            }
            else if (uri.startsWith("/pick_quest", studentRoot.length())) {
                response = handlePickingNewQuestByStudent(httpExchange, student);

            }
            else if (uri.startsWith("/display_students_by_mentor", studentRoot.length())) {
//                response = displayStudentsByMentor(httpExchange, model);
            }
        }
        view.sendResponse(response, httpExchange);
    }

    private String handleUsingInventoryArtifactByStudent(HttpExchange httpExchange, Student student) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        Integer id = 0;

        for (Object key : inputs.keySet()) {
            id = Integer.valueOf(String.valueOf(key));
        }

        decreaseStudentArtifactQuantity(id, student);

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/my_inventory.twig");
        JtwigModel model = JtwigModel.newModel();
        Set studentInventory = student.getInventory().getStock().entrySet();
        model.with("studentInventory", studentInventory);
        model.with("operationStatus", "Artifact used!");
        return template.render(model);
    }

    private void decreaseStudentArtifactQuantity(Integer id, Student student) {
        StudentInventory inventory = student.getInventory();
        Set<Artifact> artifacts = inventory.getStock().keySet();
        List<Artifact> artifactsCopy = new ArrayList<>(artifacts);
        for (Iterator<Artifact> iterator = artifactsCopy.iterator(); iterator.hasNext(); ) {
            Artifact artifact = iterator.next();
            if (id == artifact.getId() && inventory.getStock().get(artifact) == 1) {
                inventory.removeArtifact(artifact);
            } else if (id == artifact.getId() && id == artifact.getId()) {
                inventory.decreaseQuantity(artifact);
            }
        }
    }

    private String handlePickingNewQuestByStudent(HttpExchange httpExchange, Student student) throws IOException {
        Map inputs = getInputsMap(httpExchange);
        List<Quest> quests = ModelDaoFactory.getByType(QuestDAO.class).getAllModels();
        Quest pickedQuest = getSelectedQuest(inputs, quests);
        student.getStudentsQuests().addItem(pickedQuest);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/available_quests.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("operationStatus", "Quest picked successfully.");
        model.with("questsList", quests);
        return template.render(model);
    }

    private Quest getSelectedQuest(Map inputs, List<Quest> quests) {
        Integer questId = 0;
        for(Object key : inputs.keySet()) {
            questId = Integer.valueOf(String.valueOf(key));
        }
        Integer finalQuestId = questId;
        Quest pickedQuest = quests.stream()
                .filter(q -> q.getId() == finalQuestId)
                .findAny()
                .orElse(null);
        return pickedQuest;
    }

    private void getPageManager(Student student, String uri, String studentRoot, String response, JtwigTemplate template, JtwigModel model, HttpExchange httpExchange ) throws IOException {
        if (uri.startsWith(studentRoot)) {
            if (uri.startsWith("/shop", studentRoot.length())) {
                response = getShopPage();

            } else if (uri.startsWith("/student_details", studentRoot.length())) {
                response = getStudentDetailsPage(student);

            } else if (uri.startsWith("/my_inventory", studentRoot.length())) {
                response = getStudentInventoryPage(student);

            } else if (uri.startsWith("/my_team", studentRoot.length())) {
                response = getStudentTeamPage(student);

            } else if (uri.startsWith("/team_inventory", studentRoot.length())) {
                response = getStudentTeamInventoryPage(student);

            } else if (uri.startsWith("/pick_quest", studentRoot.length())) {
                response = getAvailableQuestsPage();

            } else if (uri.startsWith("/active_quests", studentRoot.length())) {
                response = getActiveQuestsPage(student);

            } else if (uri.startsWith("/my_attendance", studentRoot.length())) {
                response = getStudentAttendancePage(student);

            } else if (uri.startsWith("/student", studentRoot.length())) {
                redirectToStudentPage(httpExchange);

            } else if (uri.startsWith("/login", studentRoot.length())) {
                redirectToLoginPage(httpExchange);
                return;
            }
        }
        view.sendResponse(response, httpExchange);
    }

    private String getShopPage() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/shop.twig");
        JtwigModel model = JtwigModel.newModel();
        return template.render(model);
    }

    private void redirectToLoginPage(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Location", "/login");
        httpExchange.sendResponseHeaders(302, -1);
        httpExchange.close();
    }

    private String getStudentAttendancePage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/attendance.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("attendance", student.getAttendance().getPercentageAttendance());
        return template.render(model);
    }

    private String getActiveQuestsPage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/active_quests.twig");
        JtwigModel model = JtwigModel.newModel();
        Set quests = student.getStudentsQuests().getStock().entrySet();
        model.with("questsList", quests);
        return template.render(model);
    }

    private String getAvailableQuestsPage() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/available_quests.twig");
        JtwigModel model = JtwigModel.newModel();
        List<Quest> quests = ModelDaoFactory.getByType(QuestDAO.class).getAllModels();
        model.with("questsList", quests);
        return template.render(model);
    }

    private String getStudentDetailsPage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/student_details.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("studentName", student.getFullName());
        model.with("roleDetail", student.getRole());
        model.with("idNumber", String.valueOf(student.getId()));
        model.with("emailAdress", student.getEmail());
        model.with("groupName", student.getGroup().getName());
        model.with("teamName", student.getTeam().getName());
        model.with("walletBalance", student.getWallet());
        model.with("levelName", student.getExperienceLevel());
        return template.render(model);
    }

    private String getStudentInventoryPage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/my_inventory.twig");
        JtwigModel model = JtwigModel.newModel();
        Set studentInventory = student.getInventory().getStock().entrySet();
        model.with("studentInventory", studentInventory);
        return template.render(model);
    }

    private String getStudentTeamPage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/my_team.twig");
        JtwigModel model = JtwigModel.newModel();
        List<Student> studentTeamMembers = student.getTeam().getStudents();
        model.with("team", studentTeamMembers);
        model.with("student", student);
        return template.render(model);
    }

    private String getStudentTeamInventoryPage(Student student) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/team_inventory.twig");
        JtwigModel model = JtwigModel.newModel();
        Set teamInventory = student.getTeam().getInventory().getStock().entrySet();
        if(teamInventory.size() != 0) {
            model.with("teamArtifacts", teamInventory);
        }
        return template.render(model);
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
