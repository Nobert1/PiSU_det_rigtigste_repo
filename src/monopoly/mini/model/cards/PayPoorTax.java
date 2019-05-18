package monopoly.mini.model.cards;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.Card;
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
public class PayPoorTax extends Card {

    private int taxation;

    public int getTaxation() {
        return taxation;
    }

    public void setTaxation(int taxation) {
        this.taxation = taxation;
    }

    @Override
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        //Tax is calculated by liquid funds plus properties and the buildings one it.

        try {
            double liquidity = player.getBalance();
            controller.getPaymentController().paymentToBank(player, player.getBalance() / taxation,controller);
        } finally {
            // Make sure that the card is returned to the deck even when
            // an Exception should occur!
            super.doAction(controller, player);
        }
    }
}
