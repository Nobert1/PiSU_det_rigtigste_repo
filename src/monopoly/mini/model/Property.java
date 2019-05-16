package monopoly.mini.model;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;

import java.awt.*;
import java.util.Set;

/**
 * A property which is a space that can be owned by a player.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Property extends Space {

    private int cost;
    private int rent;
    private int mortgageValue;
    private boolean owned = false;
    private Player owner;
    private boolean mortgaged = false;
    private Color color;

    //Made a color attribute since i need it for the Jframe. We can just give colors to utilities as well cause it will look good.

    /**
     * Returns the cost of this property.
     *
     * @return the cost of this property
     */
    public int getCost() {
        return cost;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
        notifyChange();
    }

    public boolean isOwned() {
        return owned;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the cost of this property.
     *
     * @param cost the new cost of this property
     */
    public void setCost(int cost) {
        this.cost = cost;
        notifyChange();
    }


    /**
     * Returns the rent to be payed for this property.
     *
     * @return the rent for this property
     */
    public int getRent() {
        return rent;
    }

    /**
     * Sets the rent for this property.
     *
     * @param rent the new rent for this property
     */
    public void setRent(int rent) {
        this.rent = rent;
        notifyChange();
    }

    /**
     * Returns the owner of this property. The value is <code>null</code>,
     * if the property currently does not have an owner.
     *
     * @return the owner of the property or <code>null</code>
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this property  to the given owner (which can be
     * <code>null</code>).
     *
     * @param player the new owner of the property
     */
    public void setOwner(Player player) {
        this.owner = player;
        notifyChange();
    }

    /**
     * Updated so it computes the right rent for properties, while informing if property is mortgaged, houses or
     * if the player has a monopoly.
     * TODO: Price for ferry and utility
     * @author s175124 & Ekkart Kindler
     * @param controller the controller in charge of the game
     * @param player the involved player
     * @throws PlayerBrokeException
     */

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        if (owner == null) {
            controller.offerToBuy(this, player);
        } else if (this.getOwner().isInPrison()){
            controller.getGui().showMessage(this.getOwner().getName() + " is in prison so you do not have to pay rent.");
        } else if (!owner.equals(player) && !this.isMortgaged()) {
            int rent = 0;
            if (this instanceof RealEstate) {
                if(((RealEstate) this).isHotel()){
                    controller.getGui().showMessage("There is a hotel on this property.");
                } else {
                    controller.getGui().showMessage("There are " + ((RealEstate) this).getHouses() + " houses.");
                } if(((RealEstate) this).getHouses() == 0){
                    Set<RealEstate> estateSet = RealEstate.getcolormap((RealEstate) this);
                    int counter = 0;
                    for (RealEstate r: estateSet) {
                        if (this.getOwner() == r.getOwner()) {
                            counter++;
                        }
                    }
                    if (counter == estateSet.size()){
                        controller.getGui().showMessage(this.getOwner().getName() + " owns all properties of this colour and rent i doubled.");
                    }
                }
                rent = ((RealEstate) this).Computerent((RealEstate) this);
            } else {
                rent = ((Utility) this).Computerent((Utility) this);
                if(rent == 4){
                    rent = rent * controller.getDiecount();
                    controller.getGui().showMessage("Only 1 utility is owned so you pay 4 times your last roll which was"
                            + controller.getDiecount());
                } else if (rent == 10){
                    rent = rent * controller.getDiecount();
                    controller.getGui().showMessage("Both utilities are owned so you pay 10 times your last roll which was"
                            + controller.getDiecount());

                }
            }
            try {
                controller.payment(player, rent, this.getOwner());
            } catch (PlayerBrokeException e) {
                //TODO do something with exceptions
            }

        } else if(owner.equals(player)) {
            controller.getGui().showMessage("You have landed on your own property.");
        } else {
            controller.getGui().showMessage(this.getName() + " is mortgaged you do not have to pay rent.");
        }

    }

    public void setMortgageValue(int mortgageValue) {
        this.mortgageValue = mortgageValue;
    }

    public int getMortgageValue() {
        return mortgageValue;
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
        if (mortgaged)
            setRent(0);
        notifyChange();
    }

    public boolean isMortgaged() {
        return mortgaged;
    }


}