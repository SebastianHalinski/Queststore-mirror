package model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.sql.Connection;

import static org.junit.Assert.*;

public class GroupDAOTest {

    private GroupDAO groupDao;

    @Mock
    Connection connection;

    @Before
    public void setUp() {
        groupDao = new GroupDAO(connection);
    }

    @Test
    public void extractModel_returnsProperParsedGroupObject() {
        String[] groupData = {"1", "2017.1"};
        Group expected = new Group(1, "2017.1");
        Group actual = groupDao.extractModel(groupData);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }
}