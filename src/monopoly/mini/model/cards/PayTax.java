package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;

import java.util.List;
import java.util.Set;

/**
 * A card that directs the player to pay tax to the bank. The tax amounts
 * 10% of the balance of the player's account.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PayTax extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        //Tax is calculated by liquid funds plus properties and the buildings one it.

        try {
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
                            sumAssets += 5 * ((RealEstate) item).getHouseprice();
                        } else {
                            sumAssets += ((RealEstate) item).getHouses() * ((RealEstate) item).getHouseprice();
                        }
                    }
                }
            }

            int taxedAmount = liquidity + sumAssets;

            controller.paymentToBank(player, taxedAmount / 10);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}
