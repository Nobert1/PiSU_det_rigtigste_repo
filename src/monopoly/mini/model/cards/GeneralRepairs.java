package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;

import java.util.Set;

/**
 * A card that withdraws cash from player corresponding to amount of houses and hotels.
 * @author s180557
 * @return
 */

public class GeneralRepairs extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {

            int repairs = 0;
            Set<Property> assets = player.getOwnedProperties();

            for (Property item : assets) {
                    if (item instanceof RealEstate && !item.isMortgaged()) {
                        if (((RealEstate) item).isHotel()) {
                            repairs += 100;
                        } else {
                            repairs += ((RealEstate) item).getHouses() * 25;
                        }
                    }
                }

            controller.getPaymentController().paymentToBank(player, repairs,controller);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}
