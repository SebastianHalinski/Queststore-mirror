package model;

import tools.DataTool;

public abstract class User extends ActiveModel {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String sessionId;

    User(int id, String firstName, String lastName, String email, String password) {
        this(firstName, lastName, password);
        setId(id);
        this.email = email;
    }

    User(String firstName, String lastName, String password) {
        setId(-1);
        this.firstName = firstName;
        this.lastName = lastName;
        email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@cc.com";
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        saveModel();
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        saveModel();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        saveModel();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        saveModel();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toString() {
        return String.format("Role: %s, Id: %s, First name: %s, Last name: %s, email: %s",
                            this.role, getId(), this.firstName, this.lastName, this.email);
    }

    public String getFullDataToString() {
        String sign = "-";
        int signMultiplier = 100;
        return String.format("\t%s, role: %s, id: %s, email: %s\n\t%s\n",
                getFullName(), role, getId(), getEmail(), DataTool.getMultipliedString(sign, signMultiplier));
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
