package monopoly.mini.controller;
import gui_main.GUI;
import monopoly.mini.database.dal.DALException;
import monopoly.mini.database.dal.GameDAO;
import monopoly.mini.model.*;
import monopoly.mini.model.cards.GoToJail;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.Game;
import monopoly.mini.view.PlayerPanel;
import monopoly.mini.view.View;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The overall controller of a Monopoly game. It provides access
 * to all basic actions and activities for the game. All other
 * activities of the game, should be implemented by referring
 * to the basic actions and activities in this class.
 *
 * Note that this controller is far from being finished and many
 * things could be done in a much nicer and cleaner way! But, it
 * shows the general idea of how the model, view (GUI), and the
 * controller could work with each other, and how different parts
 * of the game's activities can be separated from each other, so
 * that different parts can be added and extended independently
 * from each other.
 *
 * For fully implementing the game, it will probably be necessary
 * to add more of these basic actions in this class.
 *
 * The <code>doAction()</code> methods of the
 * {@link dk.dtu.compute.se.pisd.monopoly.mini.model.Space} and
 * the {@link dk.dtu.compute.se.pisd.monopoly.mini.model.Card}
 * can be implemented based on the basic actions and activities
 * of this game controller.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
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



    /**
     * General TODO - find ud af hvorfor fanden hustingen ikke virker som den skal, optimer panels så den ikke laver et panel for hver ejendom.
     * Kig på database når Alex pusher, snak med nogen om den smarteste måde at få fat i terningernes værdi på, det er vidst nok imod vmc at hente terningens værdi fra gamecontroller.
     * Chancekort skal udvides. Payment from player skal også gennemtestest. Der skal skrives test cases.
     */

    /**
     * Constructor for a controller of a game.
     *
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
     * This method will initialize the GUI. It should be called after
     * the players of the game are created. As of now, the initialization
     * assumes that the spaces of the game fit to the fields of the GUI;
     * this could eventually be changed, by creating the GUI fields
     * based on the underlying game's spaces (fields).
     */
    public void initializeGUI() {
        this.view = new View(game, gui, playerpanel);
    }

    public void databaseinteraction () throws DALException {
        String selection = gui.getUserSelection("What you wanna do ","create game", "load game");
        if (selection.equals("load game")) {
            String s = gui.getUserButtonPressed("what game would you like to load", database.generategameIDs());
            Matcher matcher = Pattern.compile("\\d+").matcher(s);
            matcher.find();
            int i = Integer.valueOf(matcher.group());
            try {
                database.getGame(i);
            } catch (DALException e) {
                e.printStackTrace();
            }
        } else if (selection.equals("create game")) {
            playerController.createPlayers(game,this);
        }
        view.createplayers();
        view.createFields();
    }

    /**
     * Method which allows the players to chose which icon and colour they would like. Colour is unique so is removed
     * from list each time it is chosen
     * @author.
     * @param game
     */
    public String[] arrayConverterString(ArrayList<String> list){
        String[] array = new String[list.size()];
        int i = 0;
        for(String s: list){
            array[i++] = s;
        }
        return array;
    }

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
//TODO: right now to pay out of jail is in card loop which means you can only pay your way out if you have a card and say no?
        boolean terminated = false;
        while (!terminated) {
            //The player choses which function they would like to do, which then calls the method
            Player player = players.get(current);
            String choice;
            String jailChoice;
            do{
                if (player.isInPrison()) {
                    if (player.getGetOutOfJailCards() > 0) {
                        jailChoice = gui.getUserSelection("You have a get out of jail card. Would you like to use it?", "Yes", "No");
                        if (jailChoice.equals("Yes")) {
                            player.setInPrison(false);
                            player.setGetOutOfJailCards(player.getGetOutOfJailCards() - 1);
                        }
                        else if (jailChoice.equals("No")){
                            jailChoice = gui.getUserSelection("Would you like to pay your way out of prison?", "yes", "no");
                            if (jailChoice.equals("yes")) {
                                player.setInPrison(false);
                                player.setBalance(player.getBalance() - 500);
                        }
                    }
                        //Needs testing, does it cost 500? - Gustav
                    }
                }
                choice = gui.getUserButtonPressed("What would you like to do " + game.getCurrentPlayer().getName()+"?",  "Trade", "Build or Sell houses", "Mortgaging", "Roll");
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
                        "Player " + winner.getName() +
                                " has won with " + winner.getBalance() + "$.");
                break;
            } else if (countActive < 1) {
                // This can actually happen in very rare conditions and only
                // if the last player makes a stupid mistake (like buying something
                // in an auction in the same round when the last but one player went
                // bankrupt)
                gui.showMessage(
                        "All players are broke.");
                break;

            }





            // TODO offer all players the options to trade etc.

            current = (current + 1) % players.size();
            game.setCurrentPlayer(players.get(current));
            if (current == 0) {
                String selection = gui.getUserSelection(
                        "A round is finished. Do you want to continue the game?",
                        "yes",
                        "no");

                if (selection.equals("no")) {
                    String name = gui.getUserString("What would you like to save the game name as?");
                    database.savegame(name);
                    gui.showMessage("game saved");
                    terminated = true;
                }
            }
        }
        dispose();
    }

    public List<Player> getPlayerList (){
        return game.getPlayers();
    }

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
     * TODO: Not finished
     * @author s175124 &s185031
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
                        "Trade", "Sell Houses", "Mortgage", "Forefit like a bitch");
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
     * TODO: needs to be looked at again and optimised with obtain cash
     * @param property the property which is for auction
     *                 The max and min amount of bid is currently not working when i have 'highest bid' instead of a raw number ex:1,5,100
     *                 It works when using mouse on screen it wont allow player to bid if it is out of range. But it is possible to press enter
     *                 even though the 'ok' button is read
     * @author s175124
     */
    public void auction(Property property) {
        // TODO give player option to bid whatever they want and obtainCash after if they do not have enough
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
        highestBidder.setName("No one");
        int counter = 0;
        while (counter < bidList.size() - 1) {
            for (int i = 0; bidList.size() > i; i++) {
                String option = gui.getUserButtonPressed("The highest bid is " + highestBid + " by " + highestBidder.getName() + ".\n"
                        + bidList.get(i).getName() + " Do you want to bid? ", "yes", "no");
                if (option.equals("yes")) {
                    while(currentBid <= highestBid) {
                        currentBid = gui.getUserInteger("The highest bid is " + highestBid + " by " + highestBidder.getName() + ".\n" +
                                bidList.get(i).getName() + ", how much would you like to bid? Must be more than the highest bid");
                        if(currentBid > highestBid) {
                            highestBid = currentBid;
                        }
                        gui.showMessage("The bid must be higher than the highest bid!");
                    }
                    highestBidder = bidList.get(i);
                    counter = 0;
                } else if (option.equals("no")) {
                    gui.showMessage("You are removed from the auction ");
                    bidList.remove(i);
                    counter++;
                }
            }
        }
        if (!highestBidder.getName().equals("No one")) {
            gui.showMessage("Congratulations " + highestBidder.getName() + " you win " + property.getName() + " for " + highestBid + " dollars!");
            highestBidder.payMoney(highestBid);
            highestBidder.addOwnedProperty(property);
            property.setOwner(highestBidder);
        } else {
            gui.showMessage("there were no bidders so it remains unowned!");
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

        // TODO We assume here, that the broke player has already sold all his houses! But, if
        // not, we could make sure at this point that all houses are removed from
        // properties (properties with houses on are not supposed to be transferred, neither
        // in a trade between players, nor when  player goes broke to another player)
        for (Property property : brokePlayer.getOwnedProperties()) {
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

        // TODO we also need to remove the houses and the mortgage from the properties

        for (Property property : player.getOwnedProperties()) {
            property.setOwner(null);
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
                view = null;
            }
            // TODO we should also dispose of the GUI here. But this works only
            //      for my private version of the GUI and not for the GUI currently
            //      deployed via Maven (or other official versions);
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
                }
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