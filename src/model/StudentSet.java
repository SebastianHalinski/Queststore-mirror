package model;

import java.util.List;
import java.util.ArrayList;

public abstract class StudentSet extends ActiveModel {

    protected String name;
    protected List<Student> students;

    public StudentSet(int id, String name) {
        this(name);
        this.id = id;
        this.students = new ArrayList<>();
    }

    StudentSet(String name) {
        this.id = -1;
        this.name = name;
        this.students = new ArrayList<>();
    }

    public String getName(){
        return  name;
    }

    public void setName(String name){
        this.name = name;
        saveModel();
    }

    public List<Student> getStudents(){
        setStudents();
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public abstract void setStudents();

    public void addStudent(Student student){
        students.add(student);
    }

    public void removeStudent(Student student){
        students.remove(student);
    }

    public String toString(){
        return String.format("Id: %s, name: %s, number of students: %s",
                this.id, this.name, getStudents().size());
    }
}
