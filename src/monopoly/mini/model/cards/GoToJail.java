package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that sends the player to jail without passing Go.
 * @author s180557
 * @return
 */
public class GoToJail extends Card {

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

            player.setInPrison(true);
            player.setNotPassingGo(true);
            controller.getPlayerController().moveToSpace(player, controller.getSpacesList().get(10),controller);
            player.setNotPassingGo(false);

        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }



}
