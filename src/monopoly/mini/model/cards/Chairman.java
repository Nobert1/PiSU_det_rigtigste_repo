package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.RealEstate;

import java.util.Set;

/**
 * A card that directs the player to pay tax to the bank. The tax amounts
 * 10% of the balance of the player's account.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Chairman extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        //Tax is calculated by liquid funds plus properties and the buildings one it.
        try {

            for (Player reciever : controller.getPlayerList()) {
                controller.payment(player, 50, reciever);
            }

        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}
