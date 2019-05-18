package monopoly.mini.database.dal;

import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IGameDAO {


    /** The CRUD operations of the class.
     */
    public void savegame(String saveName) throws DALException;
    void getGame (int gameId) throws DALException;
    void deleteSave(int gameId) throws DALException;
    public void updateGame() throws DALException;


    /**
     * Insert methods, theese are used when you save the game.
     * @param gameName
     * @param c
     * @return
     */
    public int insertIntoGame(String gameName, Connection c) throws DALException;
    public void insertintoRealEstates(int gameID, Connection c) throws DALException;
    public void insertintoUtilities(int gameID, Connection c) throws DALException;
    public void insertintoPlayers(int gameID, Connection c) throws DALException;


    /**
     * Load methods, returns the game.
     * @return
     */
    public String[] generategameIDs() throws DALException;
    public List<Space> getspaces(int gameID, Connection c) throws DALException;
    public List<Utility> getUtilites(int gameID, Connection c) throws DALException;
    public List<RealEstate> getRealEstates(int gameID, Connection c) throws DALException;
    public List<Player> getPlayers(int gameID, Connection c) throws DALException;

    /**
     * Methods for saving the game while it's running.
     * @param gameID
     * @param c
     * @throws DALException
     */

    public void updateRealEstates(int gameID, Connection c) throws DALException;
    public void updateUtilities(int gameID, Connection c) throws DALException;
    public void updatePlayers(int gameID, Connection c) throws DALException;


    /**
     * Basic methods to make objects from resultsets.
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public RealEstate makeRealestateFromResultset(ResultSet resultSet) throws SQLException;
    public Player makePlayerFromResultset(ResultSet resultSet) throws SQLException;
    public Utility makeUtilityFromResultset(ResultSet resultSet) throws SQLException;


    }
