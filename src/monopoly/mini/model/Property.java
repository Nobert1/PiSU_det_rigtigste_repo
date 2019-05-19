package monopoly.mini.model;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.exceptions.PlayerBrokeException;

import java.awt.*;

/**
 * A property which is a space that can be owned by a player.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public abstract class Property extends Space {

    private int cost;
    private int rent;
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

    public int Computerent (GameController controller) {
        //Overwritten by the sub classes.
        return -1;
    }

    /**
     * Updated so it computes the right rent for properties, while informing if property is mortgaged, houses or
     * if the player has a monopoly.
     * @author s175124 & Ekkart Kindler
     * @param controller the controller in charge of the game
     * @param player the involved player
     * @throws PlayerBrokeException
     */

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        if (owner == null) {
            controller.getPropertyController().offerToBuy(this, player, controller);
        } else if (this.getOwner().isInPrison()) {
            controller.getGui().showMessage(this.getOwner().getName() + " is in prison so you do not have to pay rent.");
        } else if (!owner.equals(player) && !this.isMortgaged()) {
            controller.getPaymentController().payment(controller.getGame().getCurrentPlayer(), this.Computerent(controller), this.getOwner(), controller);
        }
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
        notifyChange();
    }

    public boolean isMortgaged() {
        return mortgaged;
    }


}