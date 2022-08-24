package ucm.yifei.tooltfm.example;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class Test21Test {

    /**
     * Method under test: {@link Test21#cal(int, int, int, int, int)}
     */
    @Test
    public void testCal() {
        assertEquals(0, (new Test21()).cal(1, 1, 1, 1, 1));
        assertEquals(31, (new Test21()).cal(3, 1, 1, 1, 1));
        assertEquals(0, (new Test21()).cal(0, 1, 1, 1, 1));
        assertEquals(59, (new Test21()).cal(1, 1, 3, 1, 1));
        assertEquals(31, (new Test21()).cal(3, 1, 1, 1, 0));
        assertEquals(31, (new Test21()).cal(3, 1, 1, 1, 4));
        assertEquals(0, (new Test21()).cal(1, 1, 1, 1, 1));
        assertEquals(31, (new Test21()).cal(3, 1, 1, 1, 1));
        assertEquals(0, (new Test21()).cal(0, 1, 1, 1, 1));
    }

    /**
     * Method under test: {@link Test21#cal(int, int, int, int, int)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCal2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: -1
        //       at ucm.yifei.tooltfm.example.Test21.cal(Test21.java:34)
        //   In order to prevent cal(int, int, int, int, int)
        //   from throwing ArrayIndexOutOfBoundsException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   cal(int, int, int, int, int).
        //   See https://diff.blue/R013 to resolve this issue.

        (new Test21()).cal(-1, 1, 1, 1, 1);
    }

    /**
     * Method under test: {@link Test21#cal(int, int, int, int, int)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCal3() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 13
        //       at ucm.yifei.tooltfm.example.Test21.cal(Test21.java:38)
        //   In order to prevent cal(int, int, int, int, int)
        //   from throwing ArrayIndexOutOfBoundsException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   cal(int, int, int, int, int).
        //   See https://diff.blue/R013 to resolve this issue.

        (new Test21()).cal(1, 1, Integer.MIN_VALUE, 1, 1);
    }

    /**
     * Method under test: {@link Test21#cal(int, int, int, int, int)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testCal4() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: -1
        //       at ucm.yifei.tooltfm.example.Test21.cal(Test21.java:34)
        //   In order to prevent cal(int, int, int, int, int)
        //   from throwing ArrayIndexOutOfBoundsException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   cal(int, int, int, int, int).
        //   See https://diff.blue/R013 to resolve this issue.

        (new Test21()).cal(-1, 1, 1, 1, 1);
    }

}

