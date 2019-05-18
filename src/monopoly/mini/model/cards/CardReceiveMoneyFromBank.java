package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
import monopoly.mini.model.Player;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * A card that directs the bank to pay a specific amount to the player.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CardReceiveMoneyFromBank extends Card {

    private int amount;

    /**
     * Returns the amount this card directs the bank to pay to the player.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount this card directs the bank to pay to the player.
     *
     * @param amount the new amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        try {
            controller.getPaymentController().paymentFromBank(player, amount);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }

}
