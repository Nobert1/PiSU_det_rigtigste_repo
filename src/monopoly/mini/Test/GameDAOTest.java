package monopoly.mini.Test;

import monopoly.mini.MiniMonopoly;
import monopoly.mini.database.dal.DALException;
import monopoly.mini.database.dal.GameDAO;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;
import org.junit.jupiter.api.Test;
import monopoly.mini.model.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Gustav Emil Nobert s185031
 *
 * Test class for testing the database. By testing the few methods which use the other methods alot of the lines
 * gets tested by very few lines of code.
 */

class GameDAOTest {

    void setupTestGame(Game spil, Player spiller1, Player spiller2){

        MiniMonopoly mon = new MiniMonopoly();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        mon.createSpaces(spil);
        spiller1.setName("Hans");
        spiller2.setName("Grethe");
        spiller1.setPlayerID(0);
        spiller2.setPlayerID(1);
        spiller1.setColor(Color.YELLOW);
        spiller2.setColor(Color.MAGENTA);
        spiller1.setIcon("Car");
        spiller1.setIcon("UFO");
        spiller1.setCurrentPosition(spil.getSpaces().get(0));
        spiller2.setCurrentPosition(spil.getSpaces().get(0));
    }

    @Test
    void savegame() {
        Game game = new Game();
        GameDAO gameDAO = new GameDAO(game);
        Player player = new Player();
        Player player1 = new Player();
        setupTestGame(game, player, player1);



        RealEstate realEstate = game.getRealestates().get(0);
        realEstate.setHouses(2);
        realEstate.setOwner(player);

        Utility utility = game.getUtilites().get(0);
        utility.setOwner(player);
                try {
                    gameDAO.savegame("Testsave");
                } catch (DALException e) {
                    e.getMessage();
                }

        try {
            gameDAO.getGame(GameDAO.getID());
        } catch (DALException e) {
            e.getMessage();
        }
            assertEquals(player.getName(), game.getPlayers().get(0).getName());
            assertEquals(utility.getOwner(), game.getPlayers().get(0));
            assertEquals(realEstate.getHouses(), game.getRealestates().get(0).getHouses());
    }

    @Test
    void updateGame() {
        Game game = new Game();
        GameDAO gameDAO = new GameDAO(game);
        Player player = new Player();
        Player player1 = new Player();
        setupTestGame(game, player, player1);



        RealEstate realEstate = game.getRealestates().get(0);
        realEstate.setHouses(2);
        realEstate.setOwner(player);

        Utility utility = game.getUtilites().get(0);
        utility.setOwner(player);
        try {
            gameDAO.savegame("Testsave");
        } catch (DALException e) {
            e.getMessage();
        }

        try {
            gameDAO.updateGame();
            gameDAO.getGame(GameDAO.getID());
            gameDAO.deleteSave(GameDAO.getID());
        } catch (DALException e) {
            e.getMessage();
        }
        assertEquals(player.getName(), game.getPlayers().get(0).getName());
        assertEquals(utility.getOwner(), game.getPlayers().get(0));
        assertEquals(realEstate.getHouses(), game.getRealestates().get(0).getHouses());
    }


}