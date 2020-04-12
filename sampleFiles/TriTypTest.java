import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TriTypTest {

    @Test
    public void testScalene() {
        assertEquals(1, TriTyp.triang(4, 5, 6));
    }

    @Test
    public void testIsosceles() {
        assertEquals(2, TriTyp.triang(3, 3, 4));
    }

    @Test
    public void testEquilateral() {
        assertEquals(3, TriTyp.triang(1,1,1));
    }

    @Test
    public void testInvalid() {
        assertEquals(4, TriTyp.triang(1, 1, 2));
    }

    public void notATestMethod() {
        System.out.println("this method is not a test");
    }
}
