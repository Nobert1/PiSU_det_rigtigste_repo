import monopoly.mini.model.Player;
import monopoly.mini.model.Property;

import static org.junit.Assert.*;

public class PlayerTest {

    /**
     * Get/set test of player name
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void getName() {

        Player test = new Player();
        test.setName("Per");

        assertEquals("Per", test.getName());
    }

    /**
     * Get/set test of player name
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void setName() {

        Player test = new Player();
        test.setName("Per");

        assertEquals("Per", test.getName());

    }

    @org.junit.Test
    public void getColor() {
    }

    @org.junit.Test
    public void setColor() {
    }

    @org.junit.Test
    public void getCurrentPosition() {
    }

    @org.junit.Test
    public void setCurrentPosition() {
    }

    @org.junit.Test
    public void getBalance() {
    }

    @org.junit.Test
    public void setBalance() {
    }

    /**
     * Test if player can recieve money
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void receiveMoney() {

        Player receiver = new Player();
        receiver.setBalance(0);
        receiver.receiveMoney(1500);

        assertEquals(1500,receiver.getBalance());

    }

    /**
     * Test if player can pay money
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void payMoney() {

        Player payer = new Player();
        payer.setBalance(1000);
        payer.payMoney(500);

        assertEquals(500, payer.getBalance());

    }

    @org.junit.Test
    public void getOwnedProperties() {



    }

    @org.junit.Test
    public void setOwnedProperties() {
    }

    /**
     * Test if property can be added to player.
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void addOwnedProperty() {

        Player owner = new Player();
        Property CityHall = new Property();
        owner.addOwnedProperty(CityHall);
        Property owned = owner.getOwnedProperties().iterator().next();

        assertEquals(CityHall, owned);

    }

    /**
     * Test if owned property can be removed from player.
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void removeOwnedProperty() {

        Player owner = new Player();
        Property CityHall = new Property();
        owner.addOwnedProperty(CityHall);
        owner.removeOwnedProperty(CityHall);

        assertEquals(true, owner.getOwnedProperties().isEmpty());

    }

    /**
     * Test if all properties are removed
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void removeAllProperties() {

        Player owner = new Player();
        Property CityHall = new Property();
        owner.addOwnedProperty(CityHall);
        Property Museum = new Property();
        owner.addOwnedProperty(Museum);
        owner.removeAllProperties();

        assertEquals(true, owner.getOwnedProperties().isEmpty());

    }

    @org.junit.Test
    public void getOwnedCards() {
    }

    @org.junit.Test
    public void setOwnedCards() {
    }

    @org.junit.Test
    public void removeOwnedCard() {
    }

    /**
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void isBroke() {

        Player broke = new Player();
        broke.setBroke(true);

        assertEquals(true, broke.isBroke());


    }

    @org.junit.Test
    public void setBroke() {
    }

    /**
     * @author s180557
     * @return
     */

    @org.junit.Test
    public void isInPrison() {

        Player prisoner = new Player();
        prisoner.setInPrison(true);

        assertEquals(true, prisoner.isInPrison());

    }

    @org.junit.Test
    public void setInPrison() {
    }
}