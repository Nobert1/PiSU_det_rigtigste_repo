package monopoly.mini.controller;

import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class PropertyController {


    /**
     * This method implements the activity of offering a player to buy
     * a property. This is typically triggered by a player arriving on
     * an property that is not sold yet. If the player chooses not to
     * buy, the property will be set for auction.
     *
     * @param property the property to be sold
     * @param player   the player the property is offered to
     * @throws PlayerBrokeException when the player chooses to buy but could not afford it
     */
    public void offerToBuy(Property property, Player player, GameController gameController) throws PlayerBrokeException {
        // TODO We might also allow the player to obtainCash before
        // the actual offer, to see whether he can free enough cash
        // for the sale.
        if (player.getBalance() > property.getCost()) {
            String choice = gameController.getGui().getUserSelection(
                    "Player " + player.getName() +
                            ": Do you want to buy " + property.getName() +
                            " for " + property.getCost() + "$?",
                    "yes",
                    "no");

            if (choice.equals("yes")) {
                try {
                    gameController.getPaymentController().paymentToBank(player, property.getCost(), gameController);
                } catch (PlayerBrokeException e) {
                    // if the payment fails due to the player being broke,
                    // an auction (among the other players is started
                    gameController.auction(property);
                    // then the current move is aborted by casting the
                    // PlayerBrokeException again
                    throw e;
                }
                player.addOwnedProperty(property);
                property.setOwner(player);
                property.setOwned(true);
                return;
            }
        }
        // In case the player does not buy the property,
        // an auction is started
        gameController.auction(property);
    }

    /**
     * I need a method here, just not sure yet how to do it. Could also be a boolean status, probably easier to work with
     * Gustav Emil Nobert
     *
     * @param property
     */
    public void mortgageProperty(Property property, GameController gameController) {
        property.setMortgaged(true);
        gameController.getPaymentController().paymentFromBank(property.getOwner(), property.getCost() / 2);
    }

    /**
     * Just need to implement gui so that you can see which properties are mortgaged.
     *
     * @param player
     * @author s175124
     */
    public void mortgage(Player player, GameController gameController) {

        if (player.getOwnedProperties().isEmpty()) {
            gameController.getGui().showMessage("You have no properties to mortgage");
        } else {

            String choice;
            do {
                ArrayList<String> propList = new ArrayList<>();

                for (Property p : player.getOwnedProperties()) {
                    if (!p.isMortgaged()) {
                        propList.add(p.getName());
                    }
                }

                if (!propList.isEmpty()) {
                    String[] p = gameController.arrayConverterString(propList);
                    String[] propArray = Arrays.copyOf(p, p.length + 1);
                    propArray[propList.size()] = "Back";

                    //The player chooses which property they would like to mortgage. The system then checks
                    //If there are any houses built.

                    choice = gameController.getGui().getUserButtonPressed(player.getName() + " which property would you like to mortgage?", propArray);
                    if (choice.equals("Back")) {
                        break;
                    }
                    for (Property property : player.getOwnedProperties()) {
                        if (property.getName().equals(choice)) {
                            if (property instanceof RealEstate) {
                                Set<RealEstate> estateSet = RealEstate.getcolormap((RealEstate) property);
                                for (RealEstate realEstate : estateSet) {
                                    if (realEstate.getHouses() > 0 || realEstate.isHotel()) {
                                        choice = gameController.getGui().getUserButtonPressed("You are unable to mortgage this property as there are houses on one or more of the same colour. " +
                                                "\nWould you like to sell these houses for 50% of what you payed, and then mortgage?", "Yes", "No");
                                        if (!choice.equals("No")) {
                                            sellHousesMortgage((RealEstate) property,gameController);
                                        }
                                    }
                                }
                            }
                            if (!choice.equals("No")) {
                                gameController.getGui().showMessage("You will receive " + property.getCost() / 2 + " dollars.");
                                mortgageProperty(property, gameController);
                                gameController.getGui().showMessage("You have successfully mortgaged " + property.getName());
                            }
                        }
                    }
                } else {
                    gameController.getGui().showMessage("All of your properties are now mortgaged.");
                    break;
                }
            } while (!choice.equals("Back"));
        }
    }

    public void unmortgageProperty(Property property, GameController gameController) throws PlayerBrokeException {
        property.setMortgaged(false);
        gameController.getPaymentController().paymentToBank(property.getOwner(), (property.getCost()/2) + ((property.getCost()/2)/10), gameController);
    }

    public void unmortgage(Player player, GameController gameController) throws PlayerBrokeException {
        String choice = "";
        do {
            ArrayList<Property> mortgagedProperties = new ArrayList<>();
            for (Property p : player.getOwnedProperties()) {
                if (p.isMortgaged()) {
                    mortgagedProperties.add(p);
                }
            }
            if (mortgagedProperties.isEmpty()) {
                gameController.getGui().showMessage("You have no mortgaged properties.");
                choice = "Back";
            } else {
                String[] mortgagedPropertiesArr = new String[mortgagedProperties.size() + 1];
                int i = 0;
                for (Property p : mortgagedProperties) {
                    mortgagedPropertiesArr[i++] = p.getName();
                }
                mortgagedPropertiesArr[mortgagedProperties.size()] = "Back";

                choice = gameController.getGui().getUserButtonPressed("Which property would you like to unmortgage." +
                        "\nIt will cost what you received to mortgage plus 10%.", mortgagedPropertiesArr);

                for (Property p : mortgagedProperties) {
                    if (p.getName() == choice) {
                        unmortgageProperty(p,gameController);
                        gameController.getGui().showMessage("You have successfully unmortgaged " + p.getName());
                        choice = "Back";
                    }
                }

            }
        } while (choice != "Back");
    }

    /**
     * Method which sells all the houses on a colourset so the player can mortgage a property. Fixed gui problem. Is done.
     * @author s175124
     * @param realEstate
     */
    public void sellHousesMortgage(RealEstate realEstate, GameController gameController){

        //Goes through the properties and removes the houses while adding up how many there are.
        Set<RealEstate> estateSet = RealEstate.getcolormap(realEstate);
        int counter = 0;
        for (RealEstate r : estateSet) {
            if (r.getHouses() > 0) {
                counter += r.getHouses();
                r.setHouses(0);
            } else if (r.isHotel()){
                counter += 5;
                r.setHotel(false);
            }
        }
        int soldHousesValue = counter * (realEstate.getHousePrice()/2);
        gameController.getGui().showMessage("You have sold a total of " + counter + " houses. \nYou will receive " + soldHousesValue + " dollars.");
        gameController.getPaymentController().paymentFromBank(realEstate.getOwner(),soldHousesValue);
    }

    /**
     * fixed. Evt. can add hotel as the last option instead of "when you have bought 5 houses it will turn into a hotel"
     * @author s175124
     * Har rettet fra getRent til getHousePrice - Harald
     * @param player
     */
    public void buildHouses(Player player, GameController gameController) throws PlayerBrokeException{
        int counter = 0;
        //Checks how many buildable properties the player has.
        for(Property p: player.getOwnedProperties()){
            if(p instanceof RealEstate){
                checkforbuildable((RealEstate) p);
                if(((RealEstate) p).isBuildable() && !((RealEstate) p).isHotel()){
                    counter++;
                }
            }
        }
        //Creates array with properties that the player is able to build on
        int counter2 = 0;
        String[] buildList = new String[counter];
        for(Property p: player.getOwnedProperties()){
            if(p instanceof RealEstate){
                if(((RealEstate) p).isBuildable() && !((RealEstate) p).isHotel()){
                    buildList[counter2] = p.getName();
                    counter2++;
                }
            }
        }
        if(counter == 0){
            gameController.getGui().showMessage("You are unable to build on any properties");
        } else {
            String propertyChoice = gameController.getGui().getUserButtonPressed("Which property would you like to build on?", buildList);
            RealEstate property = new RealEstate();
            for(Property p: player.getOwnedProperties()){
                if (p.getName().equals(propertyChoice)) {
                    property = (RealEstate) p;
                }
            }
            String[] houseAmount = new String[property.checkMaxHouses()];
            if (houseAmount.length == 0) {
                gameController.getGui().showMessage("You can't build since you have hotels on everything in this strip!");
            } else {
            for(int i = 1; i <= houseAmount.length; i++){
                houseAmount[i-1] = String.valueOf(i);
            }

            String houseChoice = gameController.getGui().getUserButtonPressed("How many houses would you like build? Once there is built 5 houses, they will turn into a hotel." +
                    "\nThere is currently " + property.getHouses() + " houses built. The price per house is " + property.getHousePrice()+"$",houseAmount);
            gameController.getPaymentController().paymentToBank(player,property.getHousePrice()*Integer.valueOf(houseChoice),gameController);
            if(property.getHouses()+Integer.valueOf(houseChoice) < 5) {
                property.setHouses(property.getHouses() + Integer.valueOf(houseChoice));
                gameController.getGui().showMessage("There have been built " + Integer.valueOf(houseChoice) + " houses.");
            } else {
                property.setHotel(true);
                property.setHouses(0);
                gameController.getGui().showMessage("You have now built a hotel.");
            }
            }
        }
    }

    /**
     * Done. Fixed problem with mortgaged properties.
     * @author s175124 & s185031
     * @param estate
     */
    public void checkforbuildable(RealEstate estate) {
        Set<RealEstate> estateSet = RealEstate.getcolormap(estate);
        int counter = 0;
        for (RealEstate realEstate : estateSet) {
            if (realEstate.getOwner() == estate.getOwner() && !realEstate.isMortgaged()) {
                counter++;
            }
        }
        if (counter == estateSet.size()) {
            estate.setBuildable(true);
        }
    }

    /**
     * Method that sells houses. Evt. can add that selling 5 houses when hotel just says hotel.
     * @author s175124
     * @param player
     */
    public void sellHouses(Player player, GameController gameController) {
        ArrayList<RealEstate> estateList;

        estateList = new ArrayList<>();
        for (Property p : player.getOwnedProperties()) {
            if (p instanceof RealEstate) {
                if (((RealEstate) p).getHouses() > 0 || ((RealEstate) p).isHotel()) {
                    estateList.add((RealEstate) p);
                }
            }
        }
        if (estateList.isEmpty()) {
            gameController.getGui().showMessage("You have no properties with houses or hotels.");
        } else {
            String[] estateArr = gameController.arrayConverterRealestate(estateList);
            String[] houseArr;
            String chosenProperty = gameController.getGui().getUserButtonPressed("Which property would you like to sell houses from?", estateArr);
            for (RealEstate r : estateList) {
                if (r.getName().equals(chosenProperty)) {
                        houseArr = new String[r.checkMinHouses()];
                    if (houseArr.length > 0) {
                    for (int i = 0; i < houseArr.length; i++) {
                        houseArr[i] = String.valueOf(i + 1);
                    }
                    String housesToSell = gameController.getGui().getUserButtonPressed("You have chosen " + r.getName() + " where there are " + r.getHouses()
                            + " built.\n How many would you like to sell? You will receive 50% of what you played", houseArr);
                    String accept = gameController.getGui().getUserButtonPressed("Are you sure you want to sell " + housesToSell + " houses?",
                            "Yes", "no");
                    if (accept.equals("Yes")) {
                        gameController.getPaymentController().paymentFromBank(player, (Integer.valueOf(housesToSell) * (r.getHousePrice() / 2)));
                        if (r.isHotel()) {
                            r.setHotel(false);
                            r.setHouses(5 - Integer.valueOf(housesToSell));
                        } else {
                            r.setHouses(r.getHouses() - Integer.valueOf(housesToSell));
                        }
                        gameController.getGui().showMessage("You have sold " + housesToSell + " and received " +
                                Integer.valueOf(housesToSell) * (r.getHousePrice() / 2) + "dollars");
                    }
                }
                    else
                        gameController.getGui().showMessage("You cant sell on this because you have too many houses on other properties with this color strip.");
                }
            }
        }
    }


}
