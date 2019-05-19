package monopoly.mini.model;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;

import java.util.Set;

/**
 * Represents a space, where the player has to pay tax.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 * Extended by: s180557: Revisited tax calculation.
 *
 */
public class Tax extends Space {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {

        int taxedAmount;

        if (player.getCurrentPosition().getIndex() == 4) {

            int liquidity = player.getBalance();
            int sumAssets = 0;
            Set<Property> assets = player.getOwnedProperties();

            for (Property item : assets) {
                if (item.isMortgaged()) {
                    sumAssets += item.getCost()/2;
                } else {
                    sumAssets += item.getCost();
                    if (item instanceof RealEstate) {
                        if (((RealEstate) item).isHotel()) {
                            sumAssets += 5 * ((RealEstate) item).getHousePrice();
                        } else {
                            sumAssets += ((RealEstate) item).getHouses() * ((RealEstate) item).getHousePrice();
                        }
                    }
                }
            }

            taxedAmount = liquidity + sumAssets;

            if ((taxedAmount/10) < 200) {
                taxedAmount = 200;
            } else {
                taxedAmount=taxedAmount/10;
            }

        } else {
            taxedAmount = 100;
        }

        controller.getPaymentController().paymentToBank(player, taxedAmount,controller);
    }

}