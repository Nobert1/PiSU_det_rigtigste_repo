import monopoly.mini.MiniMonopoly;
import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.cards.*;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static monopoly.mini.MiniMonopoly.createSpaces;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

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
    void getText() {
    }

    @Test
    void setText() {
    }

    /**
     * Test move card
     * @author s180557
     * @return
     */

    @Test
    void doAction() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        Player curr = spil.getCurrentPlayer();

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);

            Card kort = spil.getCardDeck().get(0);
            kort.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(spil.getSpaces().get(9), spiller1.getCurrentPosition());

    }

    /**
     * Test move backwards card
     * @author s180557
     * @return
     */

    @Test
    void doActionMoveBackwards() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        spil.getCurrentPlayer().setCurrentPosition(spil.getSpaces().get(6));

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);

            Card kort = spil.getCardDeck().get(17);
            kort.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(spil.getSpaces().get(3), spiller1.getCurrentPosition());

    }

    /**
     * Test move to nearest utility card
     * @author s180557
     * @return
     */

    @Test
    void doActionMoveUtility() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        spil.getCurrentPlayer().setCurrentPosition(spil.getSpaces().get(35));

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);

            Card kort = spil.getCardDeck().get(4);
            kort.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(spil.getSpaces().get(12), spiller1.getCurrentPosition());

    }

    /**
     * Test receive money from bank card
     * @author s180557
     * @return
     */

    @Test
    void doActionReceiveMoneyFromBank() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);

            Card kort = spil.getCardDeck().get(7);
            kort.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp+100, spiller1.getBalance());

    }

    /**
     * Test elected chairman card
     * @author s180557
     * @return
     */

    @Test
    void doActionChairman() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);

            Card kort = spil.getCardDeck().get(12);
            kort.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp-50, spiller1.getBalance());

    }

    /**
     * Test chairman card by direct instanziation card
     * @author s180557
     * @return
     */

    @Test
    void doActionChairman2() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();

        GameController cont = new GameController(spil);

        Chairman chairman = new Chairman();
        chairman.setText("You have been elected chairman. Pay 50 to all players for their troubles.");

        //controller.initializeGUI();
        try {
            chairman.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp-50, spiller1.getBalance());

    }

    /**
     * Test pay repairs card
     * @author s180557
     * @return
     */

    @Test
    void doActionRepairs() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();
        spiller1.addOwnedProperty(spil.getRealestates().get(1));
        spil.getRealestates().get(1).setHouses(2);

        GameController cont = new GameController(spil);

        GeneralRepairs repairs = new GeneralRepairs();
        repairs.setText("Your properties are needing repair. Pay 25 for each house and 100 for each hotel.");

        //controller.initializeGUI();
        try {
            repairs.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp-50, spiller1.getBalance());

    }

    /**
     * Test getoutofjail card
     * @author s180557
     * @return
     */

    @Test
    void doActionGetOutOfJailCards() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);

        GameController cont = new GameController(spil);

        GetOutOfJail getOutOfJail2 = new GetOutOfJail();
        getOutOfJail2.setText("You dug a tunnel out of jail. Get out of jail.");

        //controller.initializeGUI();
        try {
            getOutOfJail2.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(1, spiller1.getGetOutOfJailCards());

    }

    /**
     * Test go to jail card
     * @author s180557
     * @return
     */

    @Test
    void doActionGoToJail() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);

        spil.getCurrentPlayer().setCurrentPosition(spil.getSpaces().get(20));

        GameController cont = new GameController(spil);

        GoToJail goToJail1 = new GoToJail();
        goToJail1.setText("You didn't stop at a stoplight. Go to jail.");

        //controller.initializeGUI();
        try {
            goToJail1.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(true, spiller1.isInPrison());

    }
    /**
     * Test "poor tax" card
     * @author s180557
     * @return
     */


    @Test
    void doActionPoorTax() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();

        GameController cont = new GameController(spil);

        PayPoorTax poorTax = new PayPoorTax();
        poorTax.setTaxation(5);
        poorTax.setText("Pay 5% poor tax!");

        //controller.initializeGUI();
        try {
            poorTax.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp-(balanceTemp/5), spiller1.getBalance());

    }

    /**
     * Test regular tax card
     * @author s180557
     * @return
     */

    @Test
    void doActionTax() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        int balanceTemp = spiller1.getBalance();
        spiller1.addOwnedProperty(spil.getRealestates().get(1));
        spil.getRealestates().get(1).setHouses(2);

        GameController cont = new GameController(spil);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");

        //controller.initializeGUI();
        try {
            tax.doAction(cont, spiller1);

        } catch (PlayerBrokeException e) {
            e.getMessage();
        }

        //Testing that the current player position changed.
        assertEquals(balanceTemp-4016, spiller1.getBalance());

    }

}