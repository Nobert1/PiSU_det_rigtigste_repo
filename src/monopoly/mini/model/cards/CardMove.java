package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that directs the player to a move to a specific space (location) of the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CardMove extends Card {

    private Space target;

    /**
     * Returns the target space to which this card directs the player to go.
     *
     * @return the target of the move
     */
    public Space getTarget() {
        return target;
    }

    /**
     * Sets the target space of this card.
     *
     * @param target the new target of the move
     */
    public void setTarget(Space target) {
        this.target = target;
    }

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {
            controller.getPlayerController().moveToSpace(player, target,controller);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }

}
