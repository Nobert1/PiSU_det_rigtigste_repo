package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that moves the player to the next nearest utility space.
 * @author s180557
 * @return
 */

public class CardMoveUtility extends Card {

    private Space target1;
    private Space target2;

    /**
     * Returns the target space to which this card directs the player to go.
     *
     * @return the target of the move
     */
    public Space getTarget1() {
        return target1;
    }

    /**
     * Sets the target space of this card.
     *
     * @param target1 the new target of the move
     */
    public void setTarget1(Space target1) {
        this.target1 = target1;
    }

    public Space getTarget2() {
        return target2;
    }

    /**
     * Sets the target space of this card.
     *
     * @param target2 the new target of the move
     */
    public void setTarget2(Space target2) {
        this.target2 = target2;
    }

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {
            if (player.getCurrentPosition().getIndex() < 12 || player.getCurrentPosition().getIndex() >= 28) {
                controller.getPlayerController().moveToSpace(player, target1,controller);
            }
            if (player.getCurrentPosition().getIndex() < 28) {
                controller.getPlayerController().moveToSpace(player, target2,controller);
            }
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }



}
