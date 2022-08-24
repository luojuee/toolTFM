package ucm.yifei.tooltfm.example;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class Test280Test {
    /**
     * Method under test: {@link Test280#moverUno(int[])}
     */
    @Test
    public void testMoverUno() {
        int[] actualMoverUnoResult = (new Test280()).moverUno(new int[]{1, 1, 1, 1});
        assertEquals(4, actualMoverUnoResult.length);
        assertEquals(-1, actualMoverUnoResult[0]);
        assertEquals(1, actualMoverUnoResult[1]);
        assertEquals(1, actualMoverUnoResult[2]);
        assertEquals(0, actualMoverUnoResult[3]);
    }

    /**
     * Method under test: {@link Test280#moverUno(int[])}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMoverUno2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: NullPointerException
        //       at ucm.yifei.tooltfm.example.Test280.moverUno(Test280.java:6)
        //   In order to prevent moverUno(int[])
        //   from throwing NullPointerException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   moverUno(int[]).
        //   See https://diff.blue/R013 to resolve this issue.

        (new Test280()).moverUno(null);
    }

    /**
     * Method under test: {@link Test280#moverUno(int[])}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testMoverUno3() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 0
        //       at ucm.yifei.tooltfm.example.Test280.moverUno(Test280.java:9)
        //   In order to prevent moverUno(int[])
        //   from throwing ArrayIndexOutOfBoundsException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   moverUno(int[]).
        //   See https://diff.blue/R013 to resolve this issue.

        (new Test280()).moverUno(new int[]{});
    }
}


