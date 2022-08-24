package ucm.yifei.tooltfm.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Test371Test {
    /**
     * Method under test: {@link Test371#Triang(int, int, int)}
     */
    @Test
    public void testTriang() {
        assertEquals(3, Test371.Triang(1, 1, 1));
        assertEquals(4, Test371.Triang(2, 1, 1));
        assertEquals(4, Test371.Triang(3, 1, 1));
        assertEquals(4, Test371.Triang(6, 1, 1));
        assertEquals(4, Test371.Triang(0, 1, 1));
        assertEquals(4, Test371.Triang(1, 2, 1));
        assertEquals(4, Test371.Triang(1, 0, 1));
        assertEquals(4, Test371.Triang(1, 1, 2));
        assertEquals(4, Test371.Triang(1, 1, 0));
        assertEquals(2, Test371.Triang(2, 2, 1));
        assertEquals(4, Test371.Triang(2, 3, 1));
        assertEquals(2, Test371.Triang(2, 1, 2));
        assertEquals(4, Test371.Triang(2, 1, 3));
        assertEquals(4, Test371.Triang(3, 2, 1));
        assertEquals(2, Test371.Triang(1, 2, 2));
        assertEquals(0, Test371.Triang(2, 3, 4));
    }
}

