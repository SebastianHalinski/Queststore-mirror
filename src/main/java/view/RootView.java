package view;

import java.io.Console;

public class RootView extends AbstractView {

    public String getLogin(){
        return getUserInput(emptyLines + doubleTab + "Enter Your login: ");
    }

    public String getPassword() {
        Console console = System.console();
        System.out.print(doubleTab + "Please enter your password: ");
        char[] password = console.readPassword();
        return String.valueOf(password);
    }
}
