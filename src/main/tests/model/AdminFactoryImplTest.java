package model;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ModelDaoFactory.class)
public class AdminFactoryImplTest {

    @InjectMocks
    private AdminFactoryImpl adminFactory;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;


    @Before
    public void setUp() {
        adminFactory = new AdminFactoryImpl();
    }

    @Test
    public void create_returnsProperlyCreatedAdminObject() throws SQLException {
        PowerMockito.mockStatic(ModelDaoFactory.class);
        Mockito.when(ModelDaoFactory.getByType(AdminDAO.class)).thenReturn(new AdminDAO(connection));
        Mockito.when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Admin expected = new Admin("admin","admin","123");
        Admin actual = adminFactory.create("admin", "admin", "123");
        assertEquals(expected.getFullDataToString(), actual.getFullDataToString());
    }

}
