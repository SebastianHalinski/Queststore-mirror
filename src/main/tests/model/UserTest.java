package model;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class UserTest {

    @Test
    public void getEmail_ShouldReturnProperlyConstructedEmailAdress() {
        User user = new Student("John", "Doe", "123");
        String expected = "john.doe@cc.com";
        assertEquals(expected, user.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constuctor_numbersInNameShouldThrowIllegalArgumentException() {
        new Student("Jo4n", "Doe", "123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_numbersInSurnameShouldThrowIllegalArgumentException() {
            new Student("John", "Do5e", "123");
    }
}