package model;

import factory.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class ArtifactTest {

    @InjectMocks
    private Artifact artifact;

    @Mock
    private ConnectionFactory connectionFactory;

    @Before
    public void setUp() {
        artifact = spy(new Artifact("tester", "testowyOpis", 100 ));
        connectionFactory = new ConnectionFactory();
    }

    @Test
    public void getPrice_ReturnsCorrectValue() {
        int expected = 100;
        assertEquals(expected, artifact.getPrice());
    }

    @Test
    public void setPrice_changesArtifactPriceToNewValue() {
        Artifact spy = spy(new Artifact("tester", "testowyOpis", 100 ));
        doNothing().when(spy).saveModel();
        int expected = 1000;
        spy.setPrice(1000);
        assertEquals(expected, spy.getPrice());
    }

    @Test
    public void artifactShouldNotHaveMinusValue(){
            Artifact expected = new Artifact("Robienie kupy","wczoraj",0);
            Artifact actual = new Artifact("Robienie kupy", "wczoraj", -100);
            assertEquals(expected, actual);
    }

    @Test
    public void shouldNotBeAbleToChangeToMinus(){
        Artifact spy = spy(new Artifact("Robienie kupy","wczoraj",0));
        doNothing().when(spy).saveModel();
        spy.setPrice(-100);
        assertEquals(0,spy.getPrice());
    }
}