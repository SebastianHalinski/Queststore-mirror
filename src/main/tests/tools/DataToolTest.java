package tools;

import model.Student;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DataToolTest {

    @Test
    public void isWhitespaceRemoved(){
        String expected = "lubie placki i ty też";
        String actual = "lubie  placki                i ty  też ";
        actual = DataTool.removeWhitespacesFromString(actual);
        assertEquals(expected,actual);
    }

    @Test
    public void isStringShortened(){
        String expected = "lubie...";
        String actual = "lubie placki i ty też";
        int cutIndex = 5;
        actual = DataTool.getShortText(cutIndex, actual);
        assertEquals(expected, actual);

    }

    @Test
    public void isStringBeingMultiplied(){
        String expected = "lodylodylody";
        String actual = "lody";
        int multiplier = 3;
        actual = DataTool.getMultipliedString(actual,multiplier);
        assertEquals(expected,actual);
    }

    @Test
    public void isKeyRetrivedByValue(){
        Integer value = 1;
        String searchedValue = new String("Yomama");

        Map<String, Integer> mapa = new HashMap<>();
        mapa.put(searchedValue,value);


        String actual = DataTool.getKeyByValue(mapa, value);
        assertEquals(searchedValue, actual);

    }

    @Test
    public void isElementInArray(){
        Integer[] myNewArray = {4,5,32,4,5,7};
        Integer searchedElement = 32;

        assertTrue(DataTool.checkIfElementInArray(myNewArray,searchedElement));
    }

}