package monopoly.mini.model.properties;

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


    // TODO to be implemented

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
     * @param utility
     * @return
     */

    public int Computerent(Utility utility) {

        int rent = 50;
        if (utility.getCost() == 200) {

            for (Utility ship : ShippingLine) {
                if (ship.getOwner() == utility.getOwner() && !ship.getName().equals(utility.getName())) {
                    rent = rent * 2;
                }
            } } else if (utility.getCost() == 150) {
            rent = 4;
            for (Utility brewery : Breweries) {
                if (brewery.getOwner() == utility.getOwner() && !brewery.getName().equals(utility.getName())) {
                    rent = 10;
                }
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
