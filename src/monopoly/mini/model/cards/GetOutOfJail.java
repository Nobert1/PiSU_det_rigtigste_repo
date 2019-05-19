package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that helps the player get out of jail.
 * @author s180557
 * @return
 */

//TODO Karl This card should not go back in the stack until used.

public class GetOutOfJail extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {
            player.setGetOutOfJailCards(player.getGetOutOfJailCards()+1);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}