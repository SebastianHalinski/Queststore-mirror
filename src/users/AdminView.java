package users;

import application.AbstractView;

public class AdminView extends AbstractView {
    public void displayMenu() {
        String[] options = {"      *** Admin's Menu ***     \n",
                            "[1] create mentor",
                            "[2] edit mentor",
                            "[3] display mentor",
                            "[4] create group",
                            "[5] manage experience levels",
                            "[0] exit\n"};

        for(String element : options) {
            System.out.println(element);
        }
    }

    public void displayEditMenu() {
        String[] options = {"      *** Mentor Editor ***     \n",
                            "[1] edit first name",
                            "[2] edit last name",
                            "[3] edit password",
                            "[4] edit email",
                            "[5] edit group",
                            "[0] exit\n"};

        for(String element : options) {
            System.out.println(element);
        }
    }

    public void showAllMentors(String[] mentors)
    {
        for (String mentor : mentors)
        {
            System.out.println(mentor);
        }
    }
}
