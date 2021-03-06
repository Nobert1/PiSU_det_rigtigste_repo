package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that pays out money to all other players.
 * @author s180557
 * @return
 */

public class Chairman extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        //Tax is calculated by liquid funds plus properties and the buildings one it.
        try {

            for (Player reciever : controller.getPlayerList()) {
                if (reciever != player) {
                controller.getPaymentController().payment(player, 50, reciever,controller);
                }
            }

        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}
