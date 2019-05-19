import monopoly.mini.MiniMonopoly;
import monopoly.mini.controller.GameController;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.properties.RealEstate;
import org.junit.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyTest {

    /**
     * @author s180557
     * @return
     */

    void setupTestGame(Game spil, Player spiller1, Player spiller2){

        MiniMonopoly mon = new MiniMonopoly();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        mon.createSpaces(spil);
        mon.createCardDeck(spil);
        spiller1.setName("Hans");
        spiller2.setName("Grethe");
        spiller1.setColor(Color.YELLOW);
        spiller2.setColor(Color.MAGENTA);
        spiller1.setIcon("CAR");
        spiller1.setIcon("UFO");
        spiller1.setCurrentPosition(spil.getSpaces().get(0));
        spiller2.setCurrentPosition(spil.getSpaces().get(0));
    }

    @Test
    public void getCost() {

        Property test = new RealEstate();
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

        Property test = new RealEstate();
        test.setCost(500);
        Player owner = new Player();
        test.setOwner(owner);

        assertEquals(owner, test.getOwner());

    }

    @Test
    public void setOwner() {
    }


    //TODO Is this test wrong or is the code?
    @Test
    public void ComputeRentOne() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        Player curr = spil.getCurrentPlayer();

        GameController cont = new GameController(spil);

        cont.setDiecount(4, 5);

        //Testing that the current player position changed.
        assertEquals(400, spil.getUtilites().get(0).Computerent(cont));
    }

    @Test
    public void ComputeRentTwo() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        Player curr = spil.getCurrentPlayer();

        GameController cont = new GameController(spil);

        spil.getUtilites().get(0).setOwner(spiller1);
        spil.getUtilites().get(1).setOwner(spiller1);

        //Testing that the current player position changed.
        assertEquals(400, spil.getUtilites().get(0).Computerent(cont));

    }
}