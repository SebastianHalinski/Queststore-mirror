package users;

import java.util.ArrayList;
import java.util.List;
import application.Table;
import application.DbManagerDAO;
import school.GroupModel;


public class MentorDAO extends UsersDAO {

    private DbManagerDAO daoManager;

    private final String DEFAULT_TABLE = Table.MENTORS.getName();
    private final Integer ID_INDEX = 0;
    private final Integer EMAIL_INDEX = 1;
    private final Integer FIRST_NAME_INDEX = 2;
    private final Integer LAST_NAME_INDEX = 3;
    private final Integer PASSWORD_INDEX = 4;

    private int mentorId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private GroupModel group;
    private int groupId;

    public List<MentorModel> getManyObjects(List<String[]> dataCollection) {

        ArrayList<MentorModel> mentors = new ArrayList<MentorModel>();

        for (String [] record : dataCollection) {
            MentorModel mentor = getOneObject(record);
            mentors.add(mentor);
        }
        return mentors;
    }

    public List<MentorModel> getManyObjects(String query) {
        daoManager = new DbManagerDAO();
        List<String[]> dataCollection = daoManager.getData(query);
        List<MentorModel> mentors = new ArrayList<MentorModel>();
        for (String[] record : dataCollection) {
            MentorModel mentor = getOneObject(record);
            mentors.add(mentor);
        }
        return mentors;
    }

    public MentorModel getOneObject(String query) {
        daoManager = new DbManagerDAO();
        String[] mentorData = daoManager.getData(query).get(0);
        mentorId = Integer.parseInt(mentorData[ID_INDEX]);
        firstName = mentorData[FIRST_NAME_INDEX];
        lastName = mentorData[LAST_NAME_INDEX];
        email = mentorData[EMAIL_INDEX];
        password = mentorData[PASSWORD_INDEX];
        group = new GroupModel(1, "undefined", new ArrayList<StudentModel>());

        return new MentorModel(mentorId, firstName, lastName, email, password, group);
    }

    public MentorModel getOneObject(String[] record) {
        mentorId = Integer.parseInt(record[ID_INDEX]);
        firstName = record[FIRST_NAME_INDEX];
        lastName = record[LAST_NAME_INDEX];
        email = record[EMAIL_INDEX];
        password = record[PASSWORD_INDEX];
        group = new GroupModel(1, "undefined", new ArrayList<StudentModel>());

        return new MentorModel(mentorId, firstName, lastName, email, password, group);
    }

    public void saveObject(MentorModel mentor) {
        String mentorId = String.valueOf(mentor.getId());
        firstName = mentor.getFirstName();
        lastName = mentor.getLastName();
        email = mentor.getEmail();
        password = mentor.getPassword();
        groupId = mentor.getGroup().getId();

        String query;
        if(mentorId.equals("-1")) {
            query = String.format(
                    "INSERT INTO %s VALUES(null, '%s', '%s', '%s', '%s', %s);",
                    DEFAULT_TABLE, firstName, lastName, email, password, groupId);
        } else {
            query = String.format("UPDATE %s SET first_name='%s' , last_name='%s', email='%s', password='%s', group_id=%s " +
                    "WHERE id=%s;", DEFAULT_TABLE, firstName, lastName, email, password, groupId, mentorId);
        }
        daoManager = new DbManagerDAO();
        daoManager.inputData(query);
    }

    public void saveObjects(List<MentorModel> mentors) {

        for(MentorModel mentor : mentors) {
            saveObject(mentor);
        }
    }


}