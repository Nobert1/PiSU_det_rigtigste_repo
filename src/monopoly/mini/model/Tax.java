package monopoly.mini.model;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * Represents a space, where the player has to pay tax.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Tax extends Space {

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        // TODO note that tax concerns all assets an not just cash
        controller.getPaymentController().paymentToBank(player, player.getBalance() / 10,controller);
    }

}