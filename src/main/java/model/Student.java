
package model;

import enums.Role;
import controllers.ExperienceLevelsController;

public class Student extends User {

    private Group group;
    private Team team;
    private int wallet;
    private int experience;
    private StudentInventory inventory;
    private Attendance attendance;
    private String experienceLevel;
    private StudentsQuests studentsQuests;

    Student(String firstName, String lastName, String password) {
        super(firstName, lastName, password);
        wallet = 0;
        experience = 0;
        team = new Team(1, "undefined");
        group = new Group(1, "undefined");
        setRole(Role.STUDENT.getName());
    }

    Student(int id, String firstName, String lastName, String email,
                   String password, int wallet, int experience,
                   Team team, Group group) {

        super(id, firstName, lastName, email, password);
        this.wallet = wallet;
        this.experience = experience;
        this.team = team;
        this.group = group;
        this.attendance = new Attendance(id);
        this.inventory = new StudentInventory(id);
        this.studentsQuests = new StudentsQuests(id);
        setRole(Role.STUDENT.getName());
    }

    public Group getGroup() {
        setGroup();
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        saveModel();
    }

    public void setGroup() {
        this.group.setStudents();
    }

    public Team getTeam() {
        setTeam();
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        saveModel();
    }

    public void setTeam() {
        this.team.setStudents();
    }

    public StudentInventory getInventory() {
        inventory.setStock();
        return inventory;
    }

    public void setInventory(StudentInventory inventory) {
        this.inventory = inventory;
    }

    public StudentsQuests getStudentsQuests() {
        studentsQuests.setStock();
        return studentsQuests;
    }

    public void setStudentsQuests(StudentsQuests studentsQuests) {
        this.studentsQuests = studentsQuests;
    }

    public void setStudentsQuests() {
        this.studentsQuests.setStock();
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int value) {
        this.wallet = value;
        saveModel();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int value) {
        this.experience = value;
        saveModel();
    }

    public void setExperienceLevel(String level, int experienceToNextLevel) {
        experienceLevel = String.format("%s (%s/%s)", level, experience, experienceToNextLevel);
    }

    public String getExperienceLevel() {
        ExperienceLevelsController.getInstance().setStudentExperienceLevel(this);
        return experienceLevel;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public void addAttendance(Boolean isPresent) {
        attendance.addAttendance(isPresent);
    }

    public String getFullDataToString() {
        return super.getFullDataToString() + String.format(
                "\t -group: %s\n\t -team: %s\n\t -wallet: %dcc\n\t" +
                        " -level: %s\n\t -%s\n", getGroup(), getTeam(),
                getWallet(), getExperienceLevel(), attendance);
    }
}