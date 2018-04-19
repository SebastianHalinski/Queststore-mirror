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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SQLProcessManager.class})
public class ArtifactDAOTest {

    @InjectMocks
    private ArtifactDAO artifactDao;

    @Mock
    private Connection connection;

    @Before
    public void setUp() {
        artifactDao = new ArtifactDAO(connection);
    }

    @Test
    public void extractModel_shouldReturnProperParsedArtifactModel() {
        String[] artifactData = {"1", "newArtifact", "B", "some description", "123"};
        Artifact artifact = artifactDao.extractModel(artifactData);
        assertEquals(1,artifact.getId());
        assertEquals("newArtifact", artifact.getName());
        assertEquals('B', artifact.getType());
        assertEquals("some description", artifact.getDescription());
        assertEquals(123, artifact.getPrice());
    }

    @Test
    public void saveModel_properOperationShouldReturnTrue() throws SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        PowerMockito.mockStatic(SQLProcessManager.class);
        PowerMockito.when(SQLProcessManager.executeUpdate(preparedStatement)).thenReturn(true);
        Artifact artifact = new Artifact(1, 'B', "newArtifact", "some description", 123);
        assertTrue(artifactDao.saveModel(artifact));
    }
}
