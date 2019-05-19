package monopoly.mini.model.properties;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Property;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A specific property, which represents real estate on which houses
 * and hotels can be built. Note that this class does not have details
 * yet and needs to be implemented.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */


/**
 * Method author s185031 - Gustav Emil Nobert
 * Udvid klassen for grund (RealEstate) med private attributer og getter- og setter-metoder som
 * tilgår og ændrer antal byggede huse på en grund. Sørg for at metoden notifyChange() bliver kaldt,
 * så snart der er en relevant ændring i grundens attributer.
 */
public class RealEstate extends Property {
    private int houses = 0;
    private int housePrice;
    private int RealestateRent;
    private boolean hotel = false;
    private boolean buildable = false;
    public int rent;
    private Color color;
    private int propertid;
    private int mortgageValue;
    private int rentHouse1;
    private int rentHouse2;
    private int rentHouse3;
    private int rentHouse4;
    private int rentHotel;


    private static Set<RealEstate> Greyproperties = new HashSet<>();
    private static Set<RealEstate> Redproperties = new HashSet<>();
    private static Set<RealEstate> Greenproperties = new HashSet<>();
    private static Set<RealEstate> Purpleproperties = new HashSet<>();
    private static Set<RealEstate> Lightblueproperties = new HashSet<>();
    private static Set<RealEstate> MagentaProerties = new HashSet<>();
    private static Set<RealEstate> LightredProperties = new HashSet<>();
    private static Set<RealEstate> YellowProperties = new HashSet<>();
    private static Set<RealEstate> WhiteProperties = new HashSet<>();




    // TODO to be implemented


    public int getPropertid() {
        return propertid;
    }

    public void setPropertid(int propertid) {
        this.propertid = propertid;
    }

    public void setHouses(int houses) {
        this.houses = houses;
        notifyChange();
    }


    public int getHouses() {
        return houses;
    }

    @Override
    public void setRent(int rent) {
        this.rent = rent;
    }

    @Override
    public int getRent() {
        return rent;
    }

    public int getHousePrice() {
        return housePrice;
    }

    public void setHousePrice(int houseprice) {
        this.housePrice = houseprice;
        notifyChange();
    }

    public void setHotel(boolean hotel) {
        this.hotel = hotel;
        notifyChange();
    }

    public boolean isHotel() {
        return hotel;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public int getMortgageValue() {
        return mortgageValue;
    }

    public void setMortgageValue(int mortgageValue) {
        this.mortgageValue = mortgageValue;
    }

    /**
     * Denne løsning knytter sig til klassen Color og er bare en måde at finde ud af hvad folk ejer i stedet for at iterere til højre og venstre.
     * Kan godt være den mangler arbejde, jeg har ikke erfaring med Enums og det er derfor ikke helt til at sige om det virker som det skal.
     * @Author Gustav
     * @param estate
     * @return
     */

    public static Set<RealEstate> getcolormap(RealEstate estate) {

        if (estate.getColor().equals(Colors.getcolor(Colors.RED)) ) {
            return Redproperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.GREEN))) {
            return Greenproperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.LIGHTBLUE))) {
            return Lightblueproperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.GREY))) {
            return Greyproperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.PURPLE))) {
            return Purpleproperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.YELLOW))) {
            return YellowProperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.LIGHTRED))) {
            return LightredProperties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.MAGENTA))) {
            return MagentaProerties;
        } else if (estate.getColor().equals(Colors.getcolor(Colors.WHITE))) {
            return WhiteProperties;
        }
        return null;
    }



    /**
     *Method that returns rent depending on the amount of houses. If there are no houses the method looks
     * if the player has a monopoly (all properties of that colour owned) and returns double rent
     * @s175124
     * @param
     * @return
     */
//TODO er huslejen allerede sat for hvis der står huse på den?
    @Override
    public int Computerent(GameController gameController) {
        rent = this.getRent();
        if((this.isHotel())) {
            gameController.getGui().showMessage("There is a hotel on this property.");
            rent = this.getHousesRent();
        } else if (this.getHouses() > 0){
            gameController.getGui().showMessage("There are " + this.getHouses() + " houses.");
            rent = this.getHousesRent();
        } if((this.getHouses() == 0)){
            Set<RealEstate> estateSet = RealEstate.getcolormap(this);
            int counter = 0;
            for (RealEstate r: estateSet) {
                if (this.getOwner() == r.getOwner()) {
                    counter++;
                }
            }
            if (counter == estateSet.size()){
                gameController.getGui().showMessage(this.getOwner().getName() + " owns all properties of this colour and rent i doubled.");
                rent = this.getRent() * 2;
            }
        }
        gameController.getGui().showMessage("The rent is " + rent);
        return rent;
    }

    public void setRents(int one, int two, int three, int four, int hotel){
        this.rentHouse1 = one;
        this.rentHouse2 = two;
        this.rentHouse3 = three;
        this.rentHouse4 = four;
        this.rentHotel = hotel;
    }

    public int getHousesRent() {
        int rent = 0;
        if (this.getHouses() == 1) {
            rent = this.getRentHouse1();
        } if (this.getHouses() == 2) {
            rent = this.getRentHouse2();
        } if (this.getHouses() == 3) {
            rent = this.getRentHouse3();
        } if (this.getHouses() == 4) {
            rent = this.getRentHouse4();
        } if (this.isHotel()) {
            rent = this.getRentHotel();
        }
        return rent;
    }

    public int getRentHouse1() {
        return rentHouse1;
    }

    public int getRentHouse2() {
        return rentHouse2;
    }

    public int getRentHouse3() {
        return rentHouse3;
    }

    public int getRentHouse4() {
        return rentHouse4;
    }

    public int getRentHotel() {
        return rentHotel;
    }

    public static void insertintoColorMap(RealEstate estate) {
        if (estate.getColor().equals(Colors.getcolor(Colors.RED))) {
            Redproperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.GREEN))) {
            Greenproperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.LIGHTBLUE))) {
            Lightblueproperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.GREY))) {
            Greyproperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.PURPLE))) {
            Purpleproperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.YELLOW))) {
            YellowProperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.LIGHTRED))) {
            LightredProperties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.MAGENTA))) {
            MagentaProerties.add(estate);
        } else if (estate.getColor().equals(Colors.getcolor(Colors.WHITE))) {
            WhiteProperties.add(estate);
        }
    }
}