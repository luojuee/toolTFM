package ucm.yifei.tooltfm.example;

import org.junit.Assert;
import org.junit.Test;
import ucm.yifei.tooltfm.example.Test446;

import static org.junit.Assert.assertEquals;

public class Test446Test {
    /**
     * Method under test: {@link ucm.yifei.tooltfm.example.Test446#t(int, int)}
     */
    @Test
    public void testT() {
        // test start with comment
        assertEquals(3, Test446.t(2, 3));
        assertEquals(1, Test446.t(1, 1));
        assertEquals(3, Test446.t(3, 3));
        assertEquals(3, Test446.t(1, 3));
        assertEquals(0, Test446.t(-1, 3));
    }
}

