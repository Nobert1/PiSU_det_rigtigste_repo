package monopoly.mini.Test;

import monopoly.mini.model.*;
import monopoly.mini.model.cards.CardMove;
import monopoly.mini.model.cards.CardReceiveMoneyFromBank;
import monopoly.mini.model.cards.PayTax;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.bind.v2.TODO;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;

import static org.junit.Assert.*;

public class PropertyTest {

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
