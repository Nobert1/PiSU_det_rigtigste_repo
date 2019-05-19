package monopoly.mini.controller;

import monopoly.mini.model.Player;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * Payment controller which has all the methods for payment
 * @author s175124
 */


public class PaymentController {

    /**
     * This method implements a payment activity to another player,
     * which involves the player to obtain some cash on the way, in case he does
     * not have enough cash available to pay right away. If he cannot free
     * enough money in the process, the player will go bankrupt.
     *
     * @param payer    the player making the payment
     * @param amount   the payed amount
     * @param receiver the beneficiary of the payment
     * @throws PlayerBrokeException when the payer goes broke by this payment
     */
    public void payment(Player payer, int amount, Player receiver, GameController gameController) throws PlayerBrokeException {
        boolean paid = false;
        if (payer.getBalance() < amount) {
            String s = gameController.getGui().getUserButtonPressed("You do not have enough cash to pay. Would you like to sell assets to get" +
                    " enough cash?", "Yes", "No");
            if (s.equals("Yes")) {
                paid = gameController.obtainCash(payer, amount - payer.getBalance());
            }
            if (s.equals("No") || !paid) {
                gameController.playerBrokeTo(payer, receiver);
                throw new PlayerBrokeException(payer);
            }
        }
        gameController.getGui().showMessage("Player " + payer.getName() + " pays " + amount + "$ to player " + receiver.getName() + ".");
        payer.payMoney(amount);
        receiver.receiveMoney(amount);

    }

    /**
     * This method implements the action of a player receiving money from
     * the bank.
     *
     * @param player the player receiving the money
     * @param amount the amount
     */
    public void paymentFromBank(Player player, int amount) {
        player.receiveMoney(amount);
    }


    /**
     * This method implements the activity of a player making a payment to
     * the bank. Note that this might involve the player to obtain some
     * cash; in case he cannot free enough cash, he will go bankrupt
     * to the bank.
     *
     * @param player the player making the payment
     * @param amount the amount
     * @throws PlayerBrokeException when the player goes broke by the payment
     */
    public void paymentToBank(Player player, int amount, GameController gameController) throws PlayerBrokeException {
        if (amount > player.getBalance()) {
            gameController.obtainCash(player, amount-player.getBalance());
            if (amount > player.getBalance()) {
                gameController.playerBrokeToBank(player);
                throw new PlayerBrokeException(player);
            }

        }
        gameController.getGui().showMessage("Player " + player.getName() + " pays " + amount + "$ to the bank.");
        player.payMoney(amount);
    }

}
