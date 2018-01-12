package users;

public abstract class UserModel
{
    private static int counter;
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private UsersDAO usersDao;

    public UserModel(String firstName, String lastName, String password)
    {
        usersDao = new UsersDAO();
        counter = usersDao.loadLastId("DataFiles/maxUserId.csv");
        id = counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        email = firstName + Integer.toString(id) + "@cc.com";
        this.password = password;
        role = "undefined";
        usersDao.saveLastId(counter, "DataFiles/maxUserId.csv");
    }

    public UserModel(int id, String firstName, String lastName, String password)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        email = firstName + Integer.toString(id) + "@cc.com";
        this.password = password;
        role = "undefined";
    }

    public int getUserID()
    {
        return id;
    }

    public String getUserFirstName()
    {
        return firstName;
    }

    public void setUserFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getUserLastName()
    {
        return lastName;
    }

    public void setUserLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getUserEmail()
    {
        return email;
    }

    public void setUserEmail(String email)
    {
        this.email = email;
    }

    public String getUserPassword()
    {
        return password;
    }

    public void setUserPassword(String password)
    {
        this.password = password;
    }

    public String getUserRole()
    {
        return role;
    }

    public void setUserRole(String role)
    {
        this.role = role;
    }

    public String toString()
    {
        return String.format("Role: %s, Id: %s, First name: %s, Last name: %s, email: %s",
                            this.role, this.id, this.firstName, this.lastName, this.email);
    }
}
