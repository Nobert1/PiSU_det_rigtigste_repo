package monopoly.mini.database.dal;

import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GameDAO implements IGameDAO {

    /**
     * Så tænker jeg her for at det ikke skal blive alt for stort at der kommer en metode der tager fra properties og skriver til dem, en til spiller etc....
     * Indtil videre har vi ikke noget at gøre med chancekort. Lav evt forbindelsen til din egen database.
     */

//TODO - fix alle de der milliarder af forbindelser der bliver åbnet og lukket. Udover det skal der også laves nye skemaer, som rent faktisk giver mening.
// TODO Mulige fixes kan være en metode der opretter en forbindelse, hvis der allerede er en sund forbindelse returnerer den forbindelsen i stedet for.
// TODO lav et statement der kører "create if not exists" med tabellerne.

    private Game game;


    public GameDAO (Game game) {
        this.game = game;
    }


    /**
     * Probably needs something like a gameID that can be referenced.
     * Right now it's probably good enough if the methods are working allright.
     *
     * @param
     * @throws DALException
     */
    @Override
    public void savegame(String saveName) throws DALException {
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            int ID = insertIntoGame(saveName, c);
            insertintoPlayers(ID, c);
            insertintoproperties(ID, c);
            insertintoRealEstates(ID, c);
            c.commit();
            //Grunden til vi bruger en forbindelse er både for performance og for atomicity.
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Not in scope for the first iteration.
     *
     * @param gameId
     * @throws DALException
     */
    @Override
    public void deleteSave(int gameId) throws DALException {
        //TODO går nok bare noget lignende et SQL statement der hedder DELETE * FROM gameID WHERE gameID=(?) og så med noget cascade.
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            PreparedStatement preparedStatement = c.prepareStatement("DELETE FROM Game WHERE gameID =?");
            preparedStatement.setInt(1, gameId);
            int row = preparedStatement.executeUpdate();
            c.commit();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }


    /**
     * Den her skal jo gerne kunne et eller andet men den er ikke helt save game, så hvad skal den her indeholde?
     * TODO make methods for this to work.
     *
     * @throws DALException
     */

    /**
     * Takes a gameID as a parameter, we need the gui to show a list of saved games. In the long run you could save with a string
     * such that it can be "xxxxxx's, yyyy's, and wwwww's game.
     * TODO this needs something that actually sets the parameters in the game.
     * TODO - here one option is making a list of RealEstates, and a list of Utilities.
     */
    @Override
    public void getGame(int gameId) throws DALException {
        try (Connection c = DataSource.getConnection()) {
            game.setPlayers(getPlayers(gameId, c));
            game.setSpaces(getspaces(gameId, c));
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Theese are methods that create objects and returns them, just like in the database assignment.
     *
     * @return
     * @throws DALException
     */
    @Override
    public List<Space> getspaces(int gameID, Connection c) throws DALException {
        List<Space> spacelist = game.getSpaces();
        List<RealEstate> estates = getRealEstates(gameID, c);
        List<Utility> utilities = getUtilites(gameID, c);
        int estatecounter = 0;
        int utilitycounter = 0;
        for (int i = 0; i < spacelist.size(); i++) {
            if (spacelist.get(i) instanceof RealEstate) {
                spacelist.set(i, estates.get(estatecounter));
                estatecounter++;
            } else if (spacelist.get(i) instanceof Utility) {
                spacelist.set(i, utilities.get(utilitycounter));
                utilitycounter++;
            }
        }
        return spacelist;
    }

    /**
     * Returns utilites
     * @param gameID
     * @return
     * @throws DALException
     */
    @Override
    public List<Utility> getUtilites(int gameID, Connection c) throws DALException {
        try {
            // try (Connection c = DataSource.getConnection()) {
            PreparedStatement statement = c.prepareStatement("SELECT * FROM Properties WHERE gameID=?");

            statement.setInt(1, gameID);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Utility> Utilitylist = new ArrayList<>();
            while (resultSet.next()) {
                Utility utility = makeUtilityFromResultset(resultSet);
                Utilitylist.add(utility);
            }
            return Utilitylist;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Returns realestates
     * @param gameID
     * @return
     * @throws DALException
     */
    @Override
    public List<RealEstate> getRealEstates(int gameID, Connection c) throws DALException {
        try {
            //  try (Connection c = DataSource.getConnection()) {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM RealEstate WHERE gameID="+ gameID);
            ArrayList<RealEstate> realEstatelist = new ArrayList<>();
            while (resultSet.next()) {
                RealEstate realEstate = makeRealestateFromResultset(resultSet);
                realEstatelist.add(realEstate);
            }
            return realEstatelist;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Returns the players.
     */
    @Override
    public List<Player> getPlayers(int gameID, Connection c) throws DALException {
        try {
            //  try (Connection c = DataSource.getConnection()) {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Player WHERE gameID=" + gameID);
            ArrayList<Player> playerList = new ArrayList<>();
            while (resultSet.next()) {
                Player player = makePlayerFromResultset(resultSet);
                playerList.add(player);
            }

            return playerList;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Produces the real estates. Not from the bottom but with the information that is not persistent.
     * @param resultSet
     * @return
     * @throws SQLException
     */
    @Override
    public RealEstate makeRealestateFromResultset(ResultSet resultSet) throws SQLException {

        RealEstate realEstate = new RealEstate();
        for (RealEstate realEstate1 : game.getRealestates()) {
            if (realEstate1.getPropertid() == resultSet.getInt("RealEstateID")) {
                realEstate = realEstate1;
                for (Player player : game.getPlayers()) {
                    if (player.getPlayerID() == resultSet.getInt("ownerID")) {
                        realEstate.setOwner(player);
                        realEstate.setOwned(true);
                        player.addOwnedProperty(realEstate);
                    }
                }
                realEstate.setHouses(resultSet.getInt("houses"));
                realEstate.setHotel(resultSet.getBoolean("hotel"));
                realEstate.setMortgaged(resultSet.getBoolean("mortgaged"));
            }
        }
        return realEstate;
    }

    /**
     * Produces the utilities. Not from the bottom but with the information that is not persistent.
     * @param resultSet
     * @return
     * @throws SQLException
     */

    @Override
    public Utility makeUtilityFromResultset(ResultSet resultSet) throws SQLException {
        //For at der skal kunne sættes en ejer kræver det han er oprettet først.
        Utility utility = new Utility();
        for (Utility utility1 : game.getUtilites()) {
            if (utility1.getPropertyid() == resultSet.getInt("PropertyID")) {
                utility = utility1;
                //Det her kan godt virke, men aner det ikke.
                for (Player player : game.getPlayers()) {
                    if (player.getPlayerID() == resultSet.getInt("ownerID")) {
                        utility.setOwner(player);
                        utility.setOwned(true);
                        player.addOwnedProperty(utility);

                    }
                }
                utility.setMortgaged(resultSet.getBoolean("mortgaged"));
            }
        }
        return utility;
    }

    /**
     * Sets every attribute of the player.
     * @param resultSet
     * @return
     * @throws SQLException
     */

    @Override
    public Player makePlayerFromResultset(ResultSet resultSet) throws SQLException {
        Player player = new Player();
        player.setPlayerID(resultSet.getInt("playerID"));
        player.setName(resultSet.getString("PlayerName"));
        player.setBalance(resultSet.getInt("balance"));
        player.setCurrentPosition(game.getSpaces().get(resultSet.getInt("position")));
        player.setInPrison(resultSet.getBoolean("injail"));
        player.setBroke(resultSet.getBoolean("broke"));
        Color color = new Color(resultSet.getInt("r"),resultSet.getInt("g"),resultSet.getInt("b"));
        player.setColor(color);
        player.setIcon(resultSet.getString("tokentype"));
        return player;
    }

    /**
     * Insert into methods.
     * @throws DALException
     */

    @Override
    public void insertintoPlayers(int gameID, Connection c) throws DALException {

        //try (Connection c = DataSource.getConnection()) {
        try {
            PreparedStatement statement = c.prepareStatement("INSERT INTO Player VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            for (Player player : game.getPlayers()) {

                //TODO Regex this perhaps?
                statement.setInt(1, player.getPlayerID());
                statement.setString(2, player.getName());
                statement.setInt(3, gameID);
                statement.setInt(4, player.getBalance());
                statement.setInt(5, player.getCurrentPosition().getIndex());
                statement.setBoolean(6, player.isInPrison());
                statement.setBoolean(7, player.isBroke());
                statement.setInt(8, player.getColor().getRed());
                statement.setInt(9, player.getColor().getGreen());
                statement.setInt(10, player.getColor().getBlue());
                statement.setString(11, player.getIcon());
                statement.addBatch();
            }

            int[] rows = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Only insert the non persistent attributes.
     *
     * @throws DALException
     */
    @Override
    public void insertintoproperties(int gameID, Connection c) throws DALException {
        try {
            //try (Connection c = DataSource.getConnection()) {
            PreparedStatement statement = c.prepareStatement("INSERT INTO Properties VALUES (?, ?, ?, ?)");
            for (Utility utility : game.getUtilites()) {
                statement.setInt(1, gameID);
                if (utility.getOwner() != null) {
                    statement.setInt(2, utility.getOwner().getPlayerID());
                } else {
                    statement.setNull(2, Types.INTEGER);
                }
                statement.setInt(3, utility.getPropertyid());
                statement.setBoolean(4, utility.isMortgaged());
                statement.addBatch();
            }
            int[] Propertiesrow = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Only insert the non persistent attributes.
     * @param gameID
     * @throws DALException
     */
    @Override
    public void insertintoRealEstates(int gameID, Connection c) throws DALException {
        try {
            // try (Connection c = DataSource.getConnection()) {
            PreparedStatement statement = c.prepareStatement("INSERT INTO RealEstate VALUES (?, ?, ?, ?, ?, ?)");
            for (RealEstate realEstate : game.getRealestates()) {
                statement.setInt(1, gameID);
                if (realEstate.getOwner() != null) {
                    statement.setInt(2, realEstate.getOwner().getPlayerID());
                } else {
                    statement.setNull(2, Types.INTEGER);
                }
                statement.setBoolean(3, realEstate.isMortgaged());
                statement.setInt(4, realEstate.getPropertid());
                statement.setInt(5, realEstate.getHouses());
                statement.setBoolean(6,realEstate.isHotel());
                statement.addBatch();
            }

            int[] Realestaterow = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    /**
     * Inserts into game.
     */
    @Override
    public int insertIntoGame(String gameName, Connection c) throws DALException{
        try {
            int ID = 0;
            PreparedStatement statement = c.prepareStatement("INSERT INTO Game VALUES (default, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, gameName);
            int row = statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            } catch (SQLException e) {
                e.getMessage();
            }
            return ID;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
    /**
     * Generate the list of games.
     * @return
     */
    @Override
    public String[] generategameIDs() throws DALException{
        try (Connection c = DataSource.getConnection()) {
            ArrayList<String> gameIDsList = new ArrayList<>();
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Game");
            while (resultSet.next()) {
                int gameid = resultSet.getInt("gameID");
                String games = gameid + " " + resultSet.getString("SaveName");
                gameIDsList.add(games);
            }
            String[] gameIdsArray = new String[gameIDsList.size()];
            int i = 0;
            for (String name : gameIDsList) {
                gameIdsArray[i] = name;
                i++;
            }

            return gameIdsArray;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
}
