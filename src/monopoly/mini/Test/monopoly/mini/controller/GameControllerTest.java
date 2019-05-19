package monopoly.mini.controller;

import monopoly.mini.database.dal.DALException;
import monopoly.mini.model.Game;
import org.junit.jupiter.api.Test;

import static monopoly.mini.MiniMonopoly.createGame;
import static monopoly.mini.MiniMonopoly.createSpaces;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

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



        Game game = createGame();
        GameController controller = new GameController(game);
        //controller.initializeGUI();
        try {
            createSpaces(game);
        //    controller.databaseinteraction();
            game.shuffleCardDeck();

            controller.play();
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
}