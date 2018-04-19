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
public class ArtifactFactoryImplTest {

    @InjectMocks
    private ArtifactFactoryImpl artifactFactory;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Before
    public void setUp() {
        artifactFactory = new ArtifactFactoryImpl();
    }

    @Test
    public void create_returnsProperlyCreatedArtifactObject() throws SQLException {
        PowerMockito.mockStatic(ModelDaoFactory.class);
        Mockito.when(ModelDaoFactory.getByType(ArtifactDAO.class)).thenReturn(new ArtifactDAO(connection));
        Mockito.when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Artifact expected = new Artifact("artifact","some description",123);
        Artifact actual = artifactFactory.create("artifact", "some description", 123);
        assertEquals(expected.getFullDataToString(), actual.getFullDataToString());
    }
}