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

    /**
     * @author Gustav Emil Nobert s185031
     * <p>
     * This class contains all the methods for the database. The methods includes updating a current one,
     * loading a game, inserting a game and deleting old saves.
     */


    private Game game;
    private static int ID;
    //This int contins the ID of the current game, the purpose of the int it to keep track of the game so it can be updated on the run.

    public GameDAO(Game game) {
        this.game = game;
    }

    public static int getID() {
        return ID;
    }


    /**
     * Takes the savename as a parameter from the user. Contains alot of insert methods for the different tables.
     *
     * @param saveName
     * @throws DALException
     */
    @Override
    public void savegame(String saveName) throws DALException {
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            CreateSchemas(c);
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


    /**
     * Delets a save, takes the game ID from a user selection. It has a correspondig trigger to ensure that all tables
     * are empty after delete on game.
     *
     * @param gameId
     * @throws DALException
     */
    @Override
    public void deleteSave(int gameId) throws DALException {
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
     * Updates the game state in the database. This method is called after a player has taken a turn. Very similar
     * to the save game method, but updates instead.
     *
     * @throws DALException
     */
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

    /**
     * Returns the non persistant information of the games. Before this method is called the game already has produced the fields
     * and the gui.
     *
     * @param gameId
     * @throws DALException
     */
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

    /**
     * Takes the current spaces and adds the data from the database to alle of them.
     *
     * @param gameID
     * @param c
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
     * Modifies the list of utilites with the non persistant data and returns them.
     *
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
     * Modifies the real estates by adding the non persistant data and returns them after.
     *
     * @param gameID
     * @return
     * @throws DALException
     */
    @Override
    public List<RealEstate> getRealEstates(int gameID, Connection c) throws DALException {
        try {
            //  try (Connection c = DataSource.getConnection()) {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM RealEstate WHERE gameID=" + gameID);
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
     * Produces the players from the database and returns them.
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
     * Produces the real Estates. First the game makes the utilities with the non persistent information, and then
     * the non persistant information is retrieved from the database.
     *
     * @param resultSet
     * @return RealEstate
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
     * Produces the utilities. First the game makes the utilities with the non persistent information, and then
     * the non persistant information is retrieved from the database.
     *
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
     *
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
     * Standard insert into method.
     *
     * @param gameID
     * @param c
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
     *
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
                statement.setBoolean(6, realEstate.isHotel());
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
    public int insertIntoGame(String gameName, Connection c) throws DALException {
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

    /**
     * Standard update method, updates the player objects.
     *
     * @param gameID
     * @param c
     * @throws DALException
     */
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

    /**
     * Standard update method for updating the non persistant utility information.
     *
     * @param gameID
     * @param c
     * @throws DALException
     */

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
     *
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
                statement.setBoolean(4, realEstate.isHotel());
                statement.setInt(6, realEstate.getPropertid());
                statement.addBatch();
            }

            int[] Realestaterow = statement.executeBatch();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }


    /**
     * Generate the list of games so that the users can pick what game they want to load.
     *
     * @return
     */
    @Override
    public String[] generategameIDs() throws DALException {
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

    public void CreateSchemas(Connection c) throws DALException {
        try {

            c.setAutoCommit(false);

            String gameTable = "CREATE TABLE if not exists Game (\n" +
                    "gameID int auto_increment PRIMARY KEY,\n" +
                    "SaveName varchar(45) UNIQUE \n" +
                    ");";

            String playerTable = "CREATE TABLE if not exists Player (\n" +
                    "playerID int NOT NULL,\n" +
                    "gameID int references Game.gameID,\n" +
                    "PlayerName varchar(45),\n" +
                    "balance int NOT NULL,\n" +
                    "position int,\n" +
                    "injail boolean,\n" +
                    "color varchar(45),\n" +
                    "tokentype varchar(45),\n" +
                    "Currentplayer boolean,\n" +
                    "CONSTRAINT pk primary key (playerID, gameID),\n" +
                    "CONSTRAINT fk FOREIGN KEY (gameID) REFERENCES Game (gameID) ON DELETE CASCADE\n" +
                    ");";

            String RealEstateTable = "CREATE TABLE if not exists RealEstate  (\n" +
                    "gameID int references Game.gameID,\n" +
                    "ownerID int references Player.playerID ON DELETE CASCADE,\n" +
                    "mortgaged boolean,\n" +
                    "RealEstateId INT NOT NULL,\n" +
                    "houses INT,\n" +
                    "hotel boolean,\n" +
                    "CONSTRAINT pk primary key (gameID, RealEstateId)\n" +
                    ");";

            String UtilityTable = "CREATE TABLE if not exists Utilities (\n" +
                    "gameID int references Game.gameID,\n" +
                    "ownerID int references Player.playerID,\n" +
                    "utilityID int NOT NULL,\n" +
                    "mortgaged boolean,\n" +
                    "CONSTRAINT pk primary key (gameID, utilityID)\n" +
                    ");\n";

            String DroptriggerStatement = "DROP TRIGGER delete_trigger;";

            String TriggerStatement =
                    "CREATE TRIGGER delete_trigger BEFORE DELETE ON Game " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "DELETE FROM RealEstate WHERE RealEstate.gameID = old.gameID; " +
                    "DELETE FROM Player WHERE Player.gameID = old.gameID; " +
                    "DELETE FROM Utilities WHERE Utilities.gameID = old.gameID; " +
                    " END;";


            PreparedStatement statement = c.prepareStatement(gameTable);
            PreparedStatement statement1 = c.prepareStatement(playerTable);
            PreparedStatement statement2 = c.prepareStatement(RealEstateTable);
            PreparedStatement statement3 = c.prepareStatement(UtilityTable);
            PreparedStatement statement4 = c.prepareStatement(DroptriggerStatement);
            PreparedStatement statement5 = c.prepareStatement(TriggerStatement);

            statement.executeUpdate();
            statement1.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();
            statement4.executeUpdate();
            statement5.executeUpdate();


        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }
}
