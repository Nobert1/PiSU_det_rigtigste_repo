package monopoly.mini.Test.monopoly.mini.controller;

//CODE FOR SIMULATING USER INPUT.
//Fjerner test user administrator privilegie
//ByteArrayInputStream in = new ByteArrayInputStream(("1234" + System.lineSeparator() + "1").getBytes());
//System.setIn(in);
//admin.changeAdminStatus();
//System.setIn(System.in);
//ByteArrayOutputStream output = new ByteArrayOutputStream();
//byte[] byteArray = output.toByteArray();

import gui_main.GUI;
import monopoly.mini.MiniMonopoly;
import monopoly.mini.controller.GameController;
import monopoly.mini.database.dal.DALException;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.properties.Colors;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static monopoly.mini.MiniMonopoly.createGame;
import static monopoly.mini.MiniMonopoly.createSpaces;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    void setupTestGame(Game spil, Player spiller1, Player spiller2){

        MiniMonopoly mon = new MiniMonopoly();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        mon.createSpaces(spil);
        spiller1.setName("Hans");
        spiller2.setName("Grethe");
        spiller1.setColor(Color.YELLOW);
        spiller2.setColor(Color.MAGENTA);
        spiller1.setIcon("Car");
        spiller1.setIcon("UFO");
        spiller1.setCurrentPosition(spil.getSpaces().get(0));
        spiller2.setCurrentPosition(spil.getSpaces().get(0));
    }

    @Test
    void initializeGUI() {
    }

    @Test
    void databaseinteraction() {
    }

    @Test
    void arrayConverterString() {
    }

    @Test
    void arrayConverterRealestate() {
    }

    @Test
    void play() {

        //Game game = createGame();
        //GameController controller = new GameController(game);

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);
        //    controller.databaseinteraction();
            spil.shuffleCardDeck();

            Player curr = spil.getCurrentPlayer();

            cont.play();

            //User has to roll for player 1 and roll for player 2 and then choose end game.

            //Testing that the current player changed.
            assertNotEquals(curr, spil.getCurrentPlayer());

            spiller1.setBroke(true);

            cont.play();





            cont.play();
        } catch (DALException e) {
            e.getMessage();
        }

    }

    @Test
    void getPlayerList() {
    }

    @Test
    void getSpacesList() {
    }

    @Test
    void takeChanceCard() {
    }

    @Test
    void returnChanceCardToDeck() {
    }

    @Test
    void obtainCash() {
    }

    @Test
    void checkIfSolvent() {
    }

    @Test
    void auction() {
    }

    @Test
    void playerBrokeTo() {
    }

    @Test
    void playerBrokeToBank() {
    }

    @Test
    void dispose() {
    }

    @Test
    void trade() {
    }

    @Test
    void propArrayStringCreator() {
    }

    @Test
    void tradeProperties() {
    }

    @Test
    void setDiecount() {
    }

    @Test
    void getDiecount() {
    }

    @Test
    void getGui() {
    }

    @Test
    void getPaymentController() {
    }

    @Test
    void getPlayerController() {
    }

    @Test
    void getPropertyController() {
    }

    @Test
    void getGame() {
    }

    @Test
    void choiceSwitch() {
    }

    @Test
    void jailHandler() {

        Game spil = new Game();
        Player spiller1 = new Player();
        Player spiller2 = new Player();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        GameController cont = new GameController(spil);

        spiller1.setInPrison(true);
        int expectedBal = spiller1.getBalance()-500;

        cont.jailHandler(spiller1);

        //User has to press "Yes" on would you like to pay your way out.

        Assert.assertEquals(expectedBal, spiller1.getBalance());

        spiller1.setInPrison(true);
        spiller1.setGetOutOfJailCards(1);

        cont.jailHandler(spiller1);

        //User has to press "Yes" to would you like to use get out of jail card.

        assertEquals(0, spiller1.getGetOutOfJailCards());

    }
}