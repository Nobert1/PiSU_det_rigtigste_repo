package monopoly.mini.controller;

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
import monopoly.mini.database.dal.DALException;
import monopoly.mini.model.Card;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.cards.CardMoveUtility;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.Colors;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static monopoly.mini.MiniMonopoly.createGame;
import static monopoly.mini.MiniMonopoly.createSpaces;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    /**
     * Method that sets up a game to be tested.
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

    /**
     * Method that tests if current player changes in a round
     * @author s180557
     * @return
     */

    @Test
    void play() {

        //Game game = createGame();
        //GameController controller = new GameController(game);

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        Player curr = spil.getCurrentPlayer();

        GameController cont = new GameController(spil);
        //controller.initializeGUI();
        try {
            createSpaces(spil);
            //controller.databaseinteraction();
            spil.shuffleCardDeck();

            cont.play();
            //User has to roll for player 1 and roll for player 2 and then choose end game.

        } catch (DALException e) {
            e.getMessage();
        }

        //Testing that the current player changed.
        assertNotEquals(curr, spil.getCurrentPlayer());

    }

    /**
     * Test if a winner is found
     * @author s180557
     * @return
     */

    @Test
    void playToWin() {

        //TODO Ikke færdig, mangler at kunne læse hvem vinderen er.
        //Game game = createGame();
        //GameController controller = new GameController(game);

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);

        GameController cont = new GameController(spil);
        //controller.initializeGUI();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            createSpaces(spil);
            //controller.databaseinteraction();
            spil.shuffleCardDeck();
            spiller1.setBroke(true);

            Player curr = spil.getCurrentPlayer();
            cont.play();

        } catch (DALException e) {
            e.getMessage();
        }

        assertEquals("Player Hans has won with "+spiller2.getBalance()+"$", output.toString());

    }

    /**
     * Test if list of players can be fetched.
     * @author s180557
     * @return
     */

    @Test
    void getPlayerList() {
        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        GameController cont = new GameController(spil);

        ArrayList testarr = new ArrayList();
        testarr.add(spiller1); testarr.add(spiller2);

        assertEquals(testarr, cont.getPlayerList());
    }

    @Test
    void getSpacesList() {

    }

    /**
     * Test if a card is removed from the deck.
     * @author s180557
     * @return
     */

    @Test
    void takeChanceCard() throws PlayerBrokeException {
        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        GameController cont = new GameController(spil);

        Card top1 = spil.getCardDeck().get(0);

        cont.takeChanceCard(spil.getCurrentPlayer());

        Card top2 = spil.getCardDeck().get(0);

        assertNotEquals(top1, top2);

        //Notice that a card pops up on the GUI.

    }

    /**
     * Test if a specific card can be put into the stack.
     * @author s180557
     * @return
     */

    @Test
    void returnChanceCardToDeck() {
        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        GameController cont = new GameController(spil);

        CardMoveUtility move5 = new CardMoveUtility();
        move5.setTarget1(spil.getSpaces().get(12));
        move5.setTarget2(spil.getSpaces().get(28));
        move5.setText("Move to nearest Brewery!");

        cont.returnChanceCardToDeck(move5);

        Card bottom = null;
        bottom = spil.getCardDeck().get(spil.getCardDeck().size()-1);

        assertEquals(move5, bottom);
    }

    /**
     * Test if money can be obtained by mortgage, sell or trades.
     * @author s180557
     * @return
     */

    @Test
    void obtainCash() {
        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        GameController cont = new GameController(spil);
        int balancePrev = spil.getCurrentPlayer().getBalance();
        spil.getCurrentPlayer().addOwnedProperty(spil.getRealestates().get(2));
        spil.getRealestates().get(2).setOwner(spil.getCurrentPlayer());
        spil.getRealestates().get(2).setHouses(2);

        //Tester om trade/mortgage/sell virker som forventet til at opnå penge i spillet.
        //Testen skal køres ved at:
        //Trade: sælg ejendom for 50 eller
        //Mortgage: mortgage ejendom for 50 eller
        //Sell: sælg huse for 50

        cont.obtainCash(spil.getCurrentPlayer(), 50);

        assertTrue(balancePrev < spiller1.getBalance());

    }

    /**
     * Test is only property owners are solvent.
     * @author s180557
     * @return
     */

    @Test
    void checkIfSolvent() {

        Game spil = new Game(); Player spiller1 = new Player(); Player spiller2 = new Player();
        setupTestGame(spil, spiller1, spiller2);
        GameController cont = new GameController(spil);
        int balancePrev = spil.getCurrentPlayer().getBalance();
        spil.getCurrentPlayer().addOwnedProperty(spil.getRealestates().get(2));
        spil.getRealestates().get(2).setOwner(spil.getCurrentPlayer());
        spil.getRealestates().get(2).setHouses(2);

        //Tester at spilleren med ejendom er solvent, hvor spilleren uden ikke er.

        assertTrue(cont.checkIfSolvent(spiller1, 50));
        assertFalse(cont.checkIfSolvent(spiller2, 50));

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

    /**
     * Test if player can get out of jail with getoutofjail card or by payment.
     * @author s180557
     * @return
     */

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