package monopoly.mini.database.dal;

import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.properties.Colors;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GameDAO implements IGameDAO {



    private Game game;
    private static int ID;
    //This int contins the ID of the current game, the purpose of the int it to keep track of the game so it can be updated on the run.

    public GameDAO (Game game) {
        this.game = game;
    }

    public static int getID() {
        return ID;
    }



    @Override
    public void savegame(String saveName) throws DALException {
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            insertIntoGame(saveName, c);
            insertintoPlayers(ID, c);
            insertintoUtilities(ID, c);
            insertintoRealEstates(ID, c);
            c.commit();
            //Grunden til vi bruger en forbindelse er både for performance og for atomicity.
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }



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

    @Override
    public void updateGame() throws DALException {
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            updatePlayers(ID, c);
            updateUtilities(ID, c);
            updateRealEstates(ID, c);
            c.commit();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void getGame(int gameId) throws DALException {
        try (Connection c = DataSource.getConnection()) {
            game.setPlayers(getPlayers(gameId, c));
            game.setSpaces(getspaces(gameId, c));
            ID = gameId;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }


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
            PreparedStatement statement = c.prepareStatement("SELECT * FROM Utilities WHERE gameID=?");

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
            if (utility1.getPropertyid() == resultSet.getInt("utilityID")) {
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
        String color = resultSet.getString("color");

        Color color1 = new Color(Colors.getcolorName(color).getRed(), Colors.getcolorName(color).getGreen(), Colors.getcolorName(color).getBlue());
        player.setColor(color1);
        player.setIcon(resultSet.getString("tokentype"));
        return player;
    }

    /**
     * Insert into methods.
     * @throws DALException
     */

    @Override
    public void insertintoPlayers(int gameID, Connection c) throws DALException {

        try {
            PreparedStatement statement = c.prepareStatement("INSERT INTO Player VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            for (Player player : game.getPlayers()) {

                statement.setInt(1, player.getPlayerID());
                statement.setInt(2, gameID);
                statement.setString(3, player.getName());
                statement.setInt(4, player.getBalance());
                statement.setInt(5, player.getCurrentPosition().getIndex());
                statement.setBoolean(6, player.isInPrison());
                statement.setString(7, Colors.getcolorName(player.getColor()));
                statement.setString(8, player.getIcon());
                statement.setBoolean(9, false);
                if (game.getCurrentPlayer() == player) {
                    statement.setBoolean(9, true);
                }
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
    public void insertintoUtilities(int gameID, Connection c) throws DALException {
        try {
            //try (Connection c = DataSource.getConnection()) {
            PreparedStatement statement = c.prepareStatement("INSERT INTO Utilities VALUES (?, ?, ?, ?)");
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
     * Only inserts the non persistent attributes.
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
     * @param gameName
     * @param c
     * @return gameID, so that the other insert methods can have a matching gameID.
     * @throws DALException
     */
    @Override
    public int insertIntoGame(String gameName, Connection c) throws DALException{
        try {
            PreparedStatement statement = c.prepareStatement("INSERT INTO Game VALUES (default, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, gameName);
            int row = statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            } catch (SQLException e) {
                e.getMessage();
            }
            return ID;
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
    @Override
    public void updatePlayers(int gameID, Connection c) throws DALException {

        //try (Connection c = DataSource.getConnection()) {
        try {
            PreparedStatement statement = c.prepareStatement("UPDATE Player SET balance = ?, position = ?, injail = ?, " +
                    "Currentplayer = ? WHERE gameID = ? AND playerID = ?;");
            statement.setInt(5, gameID);
            for (Player player : game.getPlayers()) {
                //TODO Regex this perhaps?
                statement.setInt(1, player.getBalance());
                statement.setInt(2, player.getCurrentPosition().getIndex());
                statement.setBoolean(3, player.isInPrison());
                statement.setBoolean(4, false);
                if (game.getCurrentPlayer() == player) {
                    statement.setBoolean(4, true);
                }
                statement.setInt(6, player.getPlayerID());
                statement.addBatch();
            }

            int[] rows = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }



    @Override
    public void updateUtilities(int gameID, Connection c) throws DALException {
        try {
            //try (Connection c = DataSource.getConnection()) {
            PreparedStatement statement = c.prepareStatement("UPDATE Utilities SET ownerID = ?, mortgaged = ? WHERE gameID = ? " +
                    "AND utilityID = ?;");
            statement.setInt(3, gameID);
            for (Utility utility : game.getUtilites()) {
                if (utility.getOwner() != null) {
                    statement.setInt(1, utility.getOwner().getPlayerID());
                } else {
                    statement.setNull(1, Types.INTEGER);
                }
                statement.setBoolean(2, utility.isMortgaged());
                statement.setInt(4, utility.getPropertyid());
                statement.addBatch();
            }
            int[] Propertiesrow = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }



    /**
     * One of the update methods.
     * @param gameID
     * @param c
     * @throws DALException
     */
    @Override
    public void updateRealEstates(int gameID, Connection c) throws DALException {
        try {
            PreparedStatement statement = c.prepareStatement("UPDATE RealEstate SET ownerID = ?, mortgaged = ?, houses = ?," +
                    " hotel = ? WHERE gameID = ? AND RealEstateId = ?;");
            statement.setInt(5, gameID);
            for (RealEstate realEstate : game.getRealestates()) {
                if (realEstate.getOwner() != null) {
                    statement.setInt(1, realEstate.getOwner().getPlayerID());
                } else {
                    statement.setNull(1, Types.INTEGER);
                }
                statement.setBoolean(2, realEstate.isMortgaged());
                statement.setInt(3, realEstate.getHouses());
                statement.setBoolean(4,realEstate.isHotel());
                statement.setInt(6, realEstate.getPropertid());
                statement.addBatch();
            }

            int[] Realestaterow = statement.executeBatch();
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
                String games = gameid + ".  " + resultSet.getString("SaveName");
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
