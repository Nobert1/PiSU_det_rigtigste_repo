package monopoly.mini.controller;
import gui_main.GUI;
import monopoly.mini.database.dal.DALException;
import monopoly.mini.database.dal.GameDAO;
import monopoly.mini.model.*;
import monopoly.mini.model.cards.GetOutOfJail;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.Game;
import monopoly.mini.view.PlayerPanel;
import monopoly.mini.view.View;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The overall controller of a Monopoly game. It provides access
 * to alot of the basic actions and activities for the game.
 *
 * There are other smaller controllers with different responsibilites, to limit the size of this controller.
 * This controller calls the other controllers.
 *
 */

@SuppressWarnings("Duplicates")
public class GameController {

    private Game game;

    private GUI gui;

    private View view;

    private boolean disposed = false;

    private PlayerPanel playerpanel;

    private int Diecount;

    private GameDAO database;

    private PlayerController playerController;

    private PaymentController paymentController;

    private PropertyController propertyController;

    private String SaveName;

    private int bailJail = 50;

    /**
     * Constructor for a controller of a game.
     * @param game the game
     */

    public GameController(Game game) {
        super();
        this.game = game;
        gui = new GUI();
        database = new GameDAO(game);
        playerController = new PlayerController();
        propertyController = new PropertyController();
        paymentController = new PaymentController();
    }

    /**
     * Method initalizes the GUI.
     */
    public void initializeGUI() {
        this.view = new View(game, gui, playerpanel);
    }

    /**
     * @author Gustav Emil Nobert s185031.
     * Method for interacting with the database.
     * @throws DALException
     */
    public void databaseinteraction () throws DALException {
        String selection = gui.getUserButtonPressed("What do you want to do?", "Create game", "Load game", "Delete game");

        if (selection.equals("Delete game")) {
            String[] arr = database.generategameIDs();
            if (arr.length == 0) {
                gui.showMessage("There are no saved games");
                databaseinteraction();
            } else {
                SaveName = gui.getUserSelection("Which game would you like to delete?", arr);
                Matcher matcher = Pattern.compile("\\d+").matcher(SaveName);
                matcher.find();
                int i = Integer.valueOf(matcher.group());
                //USes to find the ID of the game. The ID of the game is given as a number.
                try {
                    database.deleteSave(i);
                    databaseinteraction();
                } catch (DALException e) {
                    e.printStackTrace();
                }
            }
        } else if (selection.equals("Load game")) {
            String[] arr = database.generategameIDs();
            if (arr.length == 0) {
                gui.showMessage("There are no saved games");
                databaseinteraction();
            } else {
                SaveName = gui.getUserSelection("Which game would you like to load?", arr);
                Matcher matcher = Pattern.compile("\\d+").matcher(SaveName);
                matcher.find();
                int i = Integer.valueOf(matcher.group());
                try {
                    database.getGame(i);
                } catch (DALException e) {
                    e.printStackTrace();
                }
            }} else if (selection.equals("Create game")) {
                playerController.createPlayers(game, this);
                SaveName = gui.getUserString("What would you like to call your save? The game auto saves the game state after each turn");
                database.savegame(SaveName);
            }
        //The view creates these are it has been identified, if there is going to be coming any information from the database.
            view.createplayers();
            view.createFields();
        }

    /**
     * Converts an array to a list of strings is used when having to display buttons.
     * @author s175124
     * @param list
     */
    public String[] arrayConverterString(ArrayList<String> list){
        String[] array = new String[list.size()];
        int i = 0;
        for(String s: list){
            array[i++] = s;
        }
        return array;
    }

    /**
     * Converts an array of real estates to an array of strings.
     * @author s175124
     * @param list
     */
    public String[] arrayConverterRealestate(ArrayList<RealEstate> list){
        String[] array = new String[list.size()];
        int i = 0;
        for(RealEstate r: list){
            array[i++] = r.getName();
        }
        return array;
    }

    /**
     * The main method to start the game. The game is started with the
     * current player of the game; this makes it possible to resume a
     * game at any point.
     * @author s175124
     */

    public void play() throws DALException {
        List<Player> players = game.getPlayers();
        Player c = game.getCurrentPlayer();
        int current = 0;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (c.equals(p)) {
                current = i;
            }
        }

        boolean terminated = false;
        while (!terminated) {
            //The player choses which function they would like to do, which then calls the method
            Player player = players.get(current);
            String choice;
            do{
                //Handles player choices when in jail.
                jailHandler(player);
                choice = gui.getUserButtonPressed("What would you like to do " + game.getCurrentPlayer().getName()+"?",  "Trade", "Build or Sell houses", "Mortgaging", "Roll");
                //Switch statement for player choices.
                choiceSwitch(choice);
            }while(choice != "Roll");

            if (!player.isBroke()) {
                try {
                    playerController.makeMove(player,this);
                } catch (PlayerBrokeException e) {
                    // We could react to the player having gone broke
                }
            }
            // Check whether we have a winner
            Player winner = null;
            int countActive = 0;
            for (Player p : players) {
                if (!p.isBroke()) {
                    countActive++;
                    winner = p;
                }
            }
            if (countActive == 1) {
                gui.showMessage(
                        "Player " + winner.getName() + " has won with " + winner.getBalance() + "$.");
                break;
            } else if (countActive < 1) {

                gui.showMessage(
                        "All players are broke.");
                break;
            }
            current = (current + 1) % players.size();
            game.setCurrentPlayer(players.get(current));
            database.updateGame();

            if (current == 0) {
                String selection = gui.getUserSelection(
                        "A round is finished. Do you want to continue the game?",
                        "yes",
                        "no");

                if (selection.equals("no")) {
                    database.updateGame();
                    gui.showMessage("game saved");
                    terminated = true;
                    System.exit(0);
                }
            }
        }
        dispose();
    }

    /**
     * Switch for selecting build, mortgage, sell or roll.
     * @author s175124
     */

    public String choiceSwitch (String choice) {
        switch(choice) {
            case "Trade":
                try {
                    trade(game.getCurrentPlayer());
                } catch (PlayerBrokeException e) {

                }
                break;
            case "Build or Sell houses":
                String h = gui.getUserButtonPressed("Would you like to build or sell?","Build","Sell");
                if(h.equals("Build")){
                    try {
                        propertyController.buildHouses(game.getCurrentPlayer(),this);
                    } catch (PlayerBrokeException e) {
                    }
                } else {
                    propertyController.sellHouses(game.getCurrentPlayer(),this);
                }
                break;
            case "Mortgaging":
                String m = gui.getUserButtonPressed("Would you like to mortgage or unmortgage?", "Mortgage","Unmortgage");
                if(m.equals("Mortgage")) {
                    propertyController.mortgage(game.getCurrentPlayer(),this);
                } else {
                    try {
                        propertyController.unmortgage(game.getCurrentPlayer(),this);
                    }catch (PlayerBrokeException e){

                    }
                }
                break;
            default:
        }
        return choice;
    }
    /**
     * Offers selections depending on situation when in prison.
     * @author s180557
     * @param player
     */
    public void jailHandler (Player player) {
        String jailChoice;
        if (player.isInPrison()) {
            if (player.getGetOutOfJailCards() > 0) {
                jailChoice = gui.getUserSelection("You have a get out of jail card. Would you like to use it?", "Yes", "No");
                if (jailChoice.equals("Yes")) {
                    player.setInPrison(false);
                    player.setGetOutOfJailCards(player.getGetOutOfJailCards() - 1);
                    GetOutOfJail getOutOfJail = new GetOutOfJail();
                    getOutOfJail.setText("Your dad made a deal with the prison-guard. Get out of jail.");
                    returnChanceCardToDeck(getOutOfJail);
                }
                else if (jailChoice.equals("No")){
                    jailChoice = gui.getUserSelection("Would you like to pay your way out of prison?", "Yes", "No");
                    if (jailChoice.equals("yes")) {
                        player.setInPrison(false);
                        player.setBalance(player.getBalance() - bailJail);
                    }
                }
            }
            else if (player.getGetOutOfJailCards() == 0) {
                jailChoice = gui.getUserSelection("Would you like to pay your way out of prison?", "yes", "no");
                if (jailChoice.equals("yes")) {
                    player.setInPrison(false);
                    player.setBalance(player.getBalance() - bailJail);
                }
            }
        }
    }

    /**
     * The method that checks if it is possible to obtain enough cash.
     * @author s180557
     * @return List<Player>
     */
    public List<Player> getPlayerList (){
        return game.getPlayers();
    }
    /**
     * The method that checks if it is possible to obtain enough cash.
     * @author s180557
     * @return
     */
    public List<Space> getSpacesList (){
        return game.getSpaces();
    }

    /**
     * The method implementing the activity of taking a chance card.
     *
     * @param player the player taking a chance card
     * @throws PlayerBrokeException if the player goes broke by this activity
     */
    public void takeChanceCard(Player player) throws PlayerBrokeException {
        Card card = game.drawCardFromDeck();
        gui.displayChanceCard(card.getText());
        gui.showMessage("Player " + player.getName() + " draws a chance card.");

        try {
            card.doAction(this, player);
        } finally {
            gui.displayChanceCard("done");
        }
    }

    /**
     * This method implements the action returning a drawn card or a card keep with
     * the player for some time back to the bottom of the card deck.
     *
     * @param card returned card
     */
    public void returnChanceCardToDeck(Card card) {
        game.returnCardToDeck(card);
    }

    /**
     * @author s175124 & s185031
     * @param player
     * @param amount: Needs amount missing to be input which then is used to check if the player has enough
     *                total value to get the missing amount.
     */
    public boolean obtainCash(Player player, int amount){
        boolean solvent = checkIfSolvent(player, amount);
        int amountBefore;
        int amountAfter;
        if(!solvent){
            gui.showMessage("You do not have enough assets to get the missing money.\n");
        } else {
            do {
                String choice = gui.getUserButtonPressed("You are missing " + amount + " dollars. How would you like to get the money?",
                        "Trade", "Sell Houses", "Mortgage", "Forfeit the game");
                switch (choice) {
                    case "Trade":
                        amountBefore = player.getBalance();
                        try {
                            trade(player);
                        } catch (PlayerBrokeException e) {

                        }
                        amountAfter = player.getBalance();
                        amount -= (amountAfter - amountBefore);
                        break;
                    case "Sell Houses":
                        amountBefore = player.getBalance();
                        propertyController.sellHouses(player,this);
                        amountAfter = player.getBalance();
                        amount -= (amountAfter - amountBefore);
                        break;
                    case "Mortgage":
                        amountBefore = player.getBalance();
                        propertyController.mortgage(player,this);
                        amountAfter = player.getBalance();
                        amount -= (amountAfter - amountBefore);
                        break;
                    default:
                        solvent = false;
                        break;
                }
            } while (amount > 0 && solvent);
        }
        if(solvent){
            gui.showMessage("You now have enough cash to pay.");
        }
        return solvent;
    }

    /**
     * The method that checks if it is possible to obtain enough cash.
     * @author s175124
     * @param player
     * @param amount
     * @return
     */
    public boolean checkIfSolvent(Player player, int amount) {
        boolean solvent = true;
        int mortgageValue = 0;
        int houseValue = 0;

        //checks the value of players houses and properties
        for (Property p : player.getOwnedProperties()) {
            if (!p.isMortgaged()) {
                mortgageValue += p.getCost()/2;
                if (p instanceof RealEstate) {
                    if (((RealEstate) p).isHotel()) {
                        houseValue += 5*((RealEstate) p).getHousePrice();
                    } else {
                        houseValue += ((RealEstate) p).getHouses()*((RealEstate) p).getHousePrice();
                    }
                }
            }
        }
        int value = houseValue + mortgageValue;
        if(value < amount){
            solvent = false;
        }
        return solvent;
    }

    /**
     * This method implements the activity of auctioning a property. Works rn
     * @param property the property which is for auction
     *                 The max and min amount of bid is currently not working when i have 'highest bid' instead of a raw number ex:1,5,100
     *                 It works when using mouse on screen it wont allow player to bid if it is out of range. But it is possible to press enter
     *                 even though the 'ok' button is read
     * @author s175124
     */
    public void auction(Property property) {
        int currentBid = -1;
        int highestBid = 0;

        ArrayList<Player> bidList = new ArrayList<>(game.getPlayers());
        do{
            Player p = bidList.remove(0);
            bidList.add(p);
            if(p.equals(game.getCurrentPlayer())){
                break;
            }
        }while(true);
        //Actual bidding method
        Player highestBidder = new Player();
        int counter = 0;
        while (counter < bidList.size() - 1) {
            for (int i = 0; bidList.size() > i; i++) {
                String option = "";
                if (highestBid != 0) {
                    option = gui.getUserButtonPressed("The highest bid is " + highestBid + " by " + highestBidder.getName() + ".\n"
                        + bidList.get(i).getName() + " Do you want to bid? ", "Yes", "No");
                } else if (highestBid == 0) {
                    option = gui.getUserButtonPressed(bidList.get(i).getName() + " there is no highest bid yet, do you want to bid? ", "Yes", "No");
                }
                if (option.equals("Yes")) {
                    do {
                        if (highestBidder.getName() != null) {
                        currentBid = gui.getUserInteger("The highest bid is " + highestBid + " by " + highestBidder.getName() + ".\n" +
                                bidList.get(i).getName() + ", how much would you like to bid? Must be more than the highest bid");
                        } else {
                            currentBid = gui.getUserInteger("How much would you like to bid?");
                        }
                        if(currentBid > highestBid) {
                            gui.showMessage(bidList.get(i).getName() + " you are now the highest bidder!");
                        } else {
                            gui.showMessage("The bid must be higher than the highest bid!");
                        }
                    } while(currentBid <= highestBid);
                    highestBid = currentBid;
                    highestBidder = bidList.get(i);
                    counter = 0;
                } else if (option.equals("No")) {
                    gui.showMessage("You are removed from the auction ");
                    bidList.remove(i);
                    counter++;
                }
            }
        }
        if (highestBidder.getName() != null) {
            gui.showMessage("Congratulations " + highestBidder.getName() + " you win " + property.getName() + " for " + highestBid + " dollars!");
            highestBidder.payMoney(highestBid);
            highestBidder.addOwnedProperty(property);
            property.setOwner(highestBidder);
        } else {
            gui.showMessage("There were no bidders so it remains unowned!");
        }
    }

    /**
     * Action handling the situation when one player is broke to another
     * player. All money and properties are transferred to the other player.
     *
     * @param brokePlayer the broke player
     * @param benificiary the player who receives the money and assets
     */
    public void playerBrokeTo(Player brokePlayer, Player benificiary) {
        int amount = brokePlayer.getBalance();
        benificiary.receiveMoney(amount);
        brokePlayer.setBalance(0);
        brokePlayer.setBroke(true);

        for (Property property : brokePlayer.getOwnedProperties()) {
            if(property instanceof RealEstate){
                ((RealEstate) property).setHouses(0);
                ((RealEstate) property).setHotel(false);
            }
            property.setOwner(benificiary);
            benificiary.addOwnedProperty(property);
        }
        brokePlayer.removeAllProperties();

        while (!brokePlayer.getOwnedCards().isEmpty()) {
            game.returnCardToDeck(brokePlayer.getOwnedCards().get(0));
        }

        gui.showMessage("Player " + brokePlayer.getName() + "went broke and transfered all"
                + "assets to " + benificiary.getName());
    }

    /**
     * Action handling the situation when a player is broke to the bank.
     *
     * @param player the broke player
     */
    public void playerBrokeToBank(Player player) {

        player.setBalance(0);
        player.setBroke(true);
        for (Property property : player.getOwnedProperties()) {
            if(property instanceof RealEstate){
                ((RealEstate) property).setHouses(0);
                ((RealEstate) property).setHotel(false);
            }
            property.setOwner(null);
            property.setMortgaged(false);

        }
        player.removeAllProperties();

        gui.showMessage("Player " + player.getName() + " went broke");

        while (!player.getOwnedCards().isEmpty()) {
            game.returnCardToDeck(player.getOwnedCards().get(0));
        }
    }

    /**
     * Method for disposing of this controller and cleaning up its resources.
     */
    public void dispose() {
        if (!disposed && view != null) {
            disposed = true;
            if (view != null) {
                view.dispose();
                System.exit(0);
                view = null;
            }
        }
    }

    /**
     * Method that allows player to trade properties and money for properties and or money with other players
     * //TODO: Make sure that player can't trade property with houses on them, unless they trade the whole set
     * @author s175124
     * @param player
     */
    public void trade(Player player)throws PlayerBrokeException{
        //Player chooses which player they would like to trade
        String choosePlayer;
        String[] tradeListString = new String[game.getPlayers().size() - 1];
        int count = 0;
        for (int i = 0; i <= tradeListString.length; i++) {
            if (game.getPlayers().get(i) != player) {
                tradeListString[count] = game.getPlayers().get(i).getName();
                count++;
            }
        }
        choosePlayer = gui.getUserButtonPressed("Who would you like to trade with?", tradeListString);
        Player tradee = new Player();
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (game.getPlayers().get(i).getName() == choosePlayer) {
                tradee = game.getPlayers().get(i);
            }
        }
        //First part of trade where the player chooses what they want to trade away
        //As owned properties is a HashSet there is made and arrayList and a String[] to use for I/O
        do {
            ArrayList<String> playerPropertiesList = new ArrayList<>(player.getOwnedProperties().size());
            for (Property p : player.getOwnedProperties()) {
                playerPropertiesList.add(p.getName());
            }
            String tradeOption = "s";
            int playerPropertyCount = 0;
            int playerMoneyCount = 0;
            Property[] giveProperties = new Property[playerPropertiesList.size()];
            do {
                String[] playerPropertiesArr = playerPropertiesList.toArray(new String[playerPropertiesList.size()]);
                tradeOption = gui.getUserButtonPressed("What would you like to give in the trade? \nYou have chosen " + playerPropertyCount + " properties, " +
                        "and " + playerMoneyCount + " dollars", "Properties", "Money", "Pick what you want to trade for");
                if (tradeOption == "Properties") {
                    if (playerPropertiesList.isEmpty()) {
                        gui.showMessage("You have no more properties to trade");
                    } else {
                        String chosenProperty = gui.getUserSelection("Which property would you like to trade?", playerPropertiesArr);
                        for (Property p : player.getOwnedProperties()) {
                            if (p.getName() == chosenProperty) {
                                giveProperties[playerPropertyCount++] = p;
                                for (int i = 0; i < playerPropertiesList.size(); i++) {
                                    if (playerPropertiesList.get(i) == chosenProperty) {
                                        playerPropertiesList.remove(i);
                                    }
                                }
                            }
                        }
                    }
                } else if (tradeOption == "Money") {
                    playerMoneyCount = gui.getUserInteger("How much money would like to add to the trade?");
                }
            } while (tradeOption != "Pick what you want to trade for");

            //Second part where the player chooses what to receive from trade
            ArrayList<String> tradeePropertiesList = new ArrayList<>(tradee.getOwnedProperties().size());
            for (Property p : tradee.getOwnedProperties()) {
                tradeePropertiesList.add(p.getName());
            }
            Property[] receiveProperties = new Property[tradeePropertiesList.size()];
            int tradeePropertyCount = 0;
            int tradeeMoneyCount = 0;
            do {
                String[] tradeePropertiesArr = tradeePropertiesList.toArray(new String[tradeePropertiesList.size()]);
                tradeOption = gui.getUserButtonPressed("What would you like to receive in the trade? \nYou have chosen " + tradeePropertyCount + " properties, " +
                        "and " + tradeeMoneyCount + " dollars", "Properties", "Money", "Get approval for trade");
                if (tradeOption == "Properties") {
                    if (tradeePropertiesList.isEmpty()) {
                        gui.showMessage("You have no more properties to chose from");
                    } else {
                        String chosenProperty = gui.getUserSelection("Which property would you like to trade?", tradeePropertiesArr);
                        for (Property p : tradee.getOwnedProperties()) {
                            if (p.getName() == chosenProperty) {
                                receiveProperties[tradeePropertyCount++] = p;
                                for (int i = 0; i < tradeePropertiesList.size(); i++) {
                                    if (tradeePropertiesList.get(i) == chosenProperty) {
                                        tradeePropertiesList.remove(i);
                                    }
                                }
                            }
                        }
                    }
                } else if (tradeOption == "Money") {
                    tradeeMoneyCount = gui.getUserInteger("How much money would like to add to the trade?");
                } //Getting approval for trade followed by swapping properties and paying eachother. If the player denies and wants to
                  // renegotiate the method will start again.
            } while (tradeOption != "Get approval for trade");
            String givePropertiesString = propArrayStringCreator(giveProperties, player);
            String receivePropertiesString = propArrayStringCreator(receiveProperties, tradee);
            String s = player.getName() + " you want to trade " + givePropertiesString + " and " + playerMoneyCount + " dollars with "
                    + tradee.getName() + " for " + receivePropertiesString + " and " + tradeeMoneyCount + ".";
            String tradeAccept = gui.getUserButtonPressed(s, "Accept Trade", "Deny");
            if (tradeAccept == "Deny") {
                choosePlayer = gui.getUserButtonPressed(tradee.getName() + " has denied the trade. Would you like to renegotiate?", "Yes", "No");
            } else {
                paymentController.payment(tradee, tradeeMoneyCount, player,this);
                paymentController.payment(player, playerMoneyCount, tradee,this);
                tradeProperties(tradee, player, receiveProperties);
                tradeProperties(player, tradee, giveProperties);
                gui.showMessage("Trade is complete.");
                break;
            }
        }while(choosePlayer != "No");
    }

    /**
     * Used in trade method.
     * @author s175124
     * @param propArray
     * @return
     */
    public String propArrayStringCreator(Property[] propArray, Player player) {
        String s = "";
        for (int i = 0; i < propArray.length; i++) {
            if(propArray[i] == null){
                break;
            } else if (i+1 ==  player.getOwnedProperties().size()){
                s += propArray[i].getName();
                break;
            }
            else if (propArray[i+1] == null) {
                s += propArray[i].getName();
                break;
            }
            s += propArray[i].getName() + ", ";

        }
        return s;
    }

    /**
     * Used in the end of trade method
     * @author s175124
     * @param giver
     * @param receiver
     * @param properties
     */

    public void tradeProperties(Player giver, Player receiver, Property[] properties){
        for(Property p: properties){
            if(p == null){
                break;
            }
            giver.removeOwnedProperty(p);
            receiver.addOwnedProperty(p);
            p.setOwner(receiver);

        }
    }

    public void setDiecount(int diecount1, int diecount2) {
        Diecount = diecount1 + diecount2;
    }

    public int getDiecount() {
        return Diecount;
    }

    public GUI getGui() {
        return gui;
    }

    public PaymentController getPaymentController() {
        return paymentController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public PropertyController getPropertyController() {
        return propertyController;
    }

    public Game getGame() {
        return game;
    }
}