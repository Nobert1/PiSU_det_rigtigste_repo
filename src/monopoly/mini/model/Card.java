package monopoly.mini.model;

import monopoly.mini.controller.GameController;
import monopoly.mini.model.cards.GetOutOfJail;
import monopoly.mini.model.exceptions.PlayerBrokeException;

/**
 * Represents a community chest or a chance card of the
 * Monopoly game. The concrete cards are represented as
 * subtypes of this class.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public abstract class Card {

    private String text;

    /**
     * Returns the text of this card.
     *
     * @return the text of this card
     */
    public String getText() {
        return text;
    }

    /**
     * Changes the text of this card.
     *
     * @param text the text this card should show
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * The action involved with this card.
     *
     * @param controller the controller that is in charge of the game
     * @param player the involved player
     * @throws PlayerBrokeException when the player goes bankrupt by the action
     *
     * extended by s180557: does not return getoutofjail-cards.
     */
    public void doAction(GameController controller, Player player) throws PlayerBrokeException {
        // Most cards should return the card to the deck, when the card action
        // is executed. Subclasses and call the super method to achieve this.
        if (this.getClass() != GetOutOfJail.class) {
            controller.returnChanceCardToDeck(this);
        }
    }

}
