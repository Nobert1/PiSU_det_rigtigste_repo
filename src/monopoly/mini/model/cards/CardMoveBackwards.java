package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that moves the player backwards by three
 * @author s180557
 * @return
 */
public class CardMoveBackwards extends Card {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {

            int newPos = player.getCurrentPosition().getIndex()-3;
            Space newSpace = controller.getSpacesList().get(newPos);

            controller.getPlayerController().moveToSpace(player, newSpace,controller);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }

}
