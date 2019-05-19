import org.junit.Test;

import monopoly.mini.model.Player;
import monopoly.mini.model.Property;

import static org.junit.Assert.*;

public class PropertyTest {

    /**
     * @author s180557
     * @return
     */

    @Test
    public void getCost() {

        Property test = new Property();
        test.setCost(500);

        assertEquals(500, test.getCost());

    }

    @Test
    public void setCost() {
    }

    @Test
    public void getRent() {
    }

    @Test
    public void setRent() {
    }

    /**
     * @author s180557
     * @return
     */

    @Test
    public void getOwner() {

        Property test = new Property();
        test.setCost(500);
        Player owner = new Player();
        test.setOwner(owner);

        assertEquals(owner, test.getOwner());

    }

    @Test
    public void setOwner() {
    }

    @Test
    public void doAction() {

        //TODO To be implemented

    }
}
