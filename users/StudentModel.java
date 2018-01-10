package users;

import java.util.ArrayList;

public class StudentModel extends UserModel
{
    private String group;
    private String team;
    private int wallet;
    private int expirence;
    private ArrayList inventory;
    private float attendance;

    public StudentModel(String firstName, String lastName, String password)
    {
        super(firstName, lastName, password);
        super.setUserRole("student");
        wallet = 0;
        expirence = 0;
        attendance = 100; 
    }

    public String getGroup() 
    {
        return group;    
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getTeam()
    {
        return team;
    }

    public void setTeam(String team)
    {
        this.team = team;
    }

    public int getWallet() 
    {
        return wallet;    
    }

    public void setWallet(int value)
    {
        this.wallet = value;
    }

    public int getExpirence() 
    {
        return expirence;    
    }

    public void setExpirence(int value)
    {
        this.expirence = value;
    }

    public float getAttendance() 
    {
        return attendance;
    }

    public void setAttendance(float attendance) 
    {
        this.attendance = attendance;
    }
}