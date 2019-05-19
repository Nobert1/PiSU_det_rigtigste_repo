package monopoly.mini.model.properties;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Property;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A specific property, which represents a utility which can
 * not be developed with houses or hotels.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Utility extends Property {

    private static Set<Utility> ShippingLine = new HashSet<Utility>();
    private static Set<Utility> Breweries = new HashSet<Utility>();
    private java.awt.Color color;
    private int propertyid;

    public int getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(int propertyid) {
        this.propertyid = propertyid;
    }

    /**
     * Method for  calculating the rent of the utilities.
     * @author s185031
     * @return
     */
    @Override
    public int Computerent(GameController controller) {

        int numberofowned = 0;
        int rent = 50;
        if (this.getCost() == 200) {
            for (Utility ship : ShippingLine) {
                if (ship.getOwner() == this.getOwner() && !ship.getName().equals(this.getName()) && !ship.isMortgaged()) {
                    rent = rent * 2;
                    numberofowned++;
                    controller.getGui().showMessage("The rent is " + rent + " since that the owner has " + numberofowned + " ships.");
                }
            } } else if (this.getCost() == 150) {
            rent = 4 * controller.getDiecount();
            int counter = 0;
            for (Utility brewery : Breweries) {
                if (brewery.getOwner() == this.getOwner() && !brewery.isMortgaged()) {
                    counter++;
                }
            }
            if (counter == 2) {
                rent = 10 * controller.getDiecount();
                controller.getGui().showMessage("Since the owner has two breweries, the rent is " + rent + " which is the diecount * 10");
            } else {
                controller.getGui().showMessage("Since the owner has one brewery, the rent is " + rent + " which is the diecount * 4");
            }
        }
        return rent;
    }

    public static Set<Utility> getBreweries() {
        return Breweries;
    }

    public static Set<Utility> getShippingLine() {
        return ShippingLine;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
