import monopoly.mini.model.Space;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpaceTest {

    /**
     * @author s180557
     * @return
     */

    @Test
    public void getName() {

        Space test = new Space();
        test.setName("Spacey");

        assertEquals("Spacey", test.getName());

    }

    @Test
    public void setName() {
    }

    /**
     * @author s180557
     * @return
     */

    @Test
    public void getIndex() {

        Space test = new Space();
        test.setIndex(23);

        assertEquals(23, test.getIndex());

    }

    @Test
    public void setIndex() {
    }

    @Test
    public void doAction() {

        //TODO: To be implemented (depends on Tax)

    }
}