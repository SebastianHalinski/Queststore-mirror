package model;

import managers.SQLProcessManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SQLProcessManager.class})
public class AdminDAOTest {

    @InjectMocks
    private AdminDAO adminDao;

    @Mock
    Connection connection;

    @Before
    public void setUP() {
        adminDao = new AdminDAO(connection);
    }

    @Test
    public void extractModel_shouldReturnProperParsedAdminModel() {
        String[] adminData = {"1", "admin", "admin", "admin@cc.com", "123"};
        Admin admin = adminDao.extractModel(adminData);
        assertEquals(1,admin.getId());
        assertEquals("admin", admin.getFirstName());
        assertEquals("admin", admin.getLastName());
        assertEquals("admin@cc.com", admin.getEmail());
        assertEquals("123", admin.getPassword());
    }

    @Test
    public void saveModel_properOperationShouldReturnTrue() throws SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        PowerMockito.mockStatic(SQLProcessManager.class);
        PowerMockito.when(SQLProcessManager.executeUpdate(preparedStatement)).thenReturn(true);
        Admin admin = new Admin(1, "admin", "admin", "admin@cc.com", "123");
        assertTrue(adminDao.saveModel(admin));
    }

    @Test
    public void saveModel_unproperSQLOperationShouldReturnFalse() throws SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        Admin admin = new Admin(1, "admin", "admin", "admin@cc.com", "123");
        assertFalse(adminDao.saveModel(admin));
    }
}