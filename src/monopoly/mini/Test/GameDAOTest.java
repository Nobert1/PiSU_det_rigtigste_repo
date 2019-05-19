package monopoly.mini.Test;

import monopoly.mini.database.dal.DALException;
import monopoly.mini.database.dal.GameDAO;
import monopoly.mini.model.properties.Colors;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;
import org.junit.jupiter.api.Test;
import monopoly.mini.model.*;
import monopoly.mini.database.*;

import java.awt.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    @Test
    void savegame() {
        Game game = new Game();
        GameDAO gameDAO = new GameDAO(game);
        Player player = new Player();
        player.setInPrison(false);
        player.setBalance(2000);
        player.setName("Egon");
        player.setCurrentPosition(game.getSpaces().get(0));
        player.setBroke(false);
        player.setColor(Colors.getcolor(Colors.RED));
        player.setGetOutOfJailCards(0);
        player.setIcon("UFO");

        RealEstate realEstate = new RealEstate();
        realEstate.setPropertid(2);
        realEstate.setHouses(2);
        realEstate.setHotel(false);
        realEstate.setOwner(player);

        Utility utility = new Utility();
        utility.setPropertyid(1);
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
            assertEquals(player.getBalance(), game.getPlayers().get(0).getBalance());
            assertEquals(utility.getPropertyid(), game.getRealestates().get(0).getPropertid());
            assertEquals(realEstate.getPropertid(), game.getUtilites().get(0).getPropertyid());
    }

    @Test
    void updateGame() {
    }

    @Test
    void getGame() {
    }
}