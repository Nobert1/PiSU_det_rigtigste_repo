import monopoly.mini.model.*;
import monopoly.mini.model.cards.CardMove;
import monopoly.mini.model.cards.CardReceiveMoneyFromBank;
import monopoly.mini.model.cards.PayTax;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    /**
     * Test if spaces are set as expected and can be fetched.
     * @author s180557
     * @return
     */

    @Test
    void getSpaces() {

        Game spil = new Game();

        Space go = new Space();
        go.setName("Go");
        spil.addSpace(go);

        Property p = new Property();
        p.setName("Rødovrevej");
        p.setCost(1200);
        p.setRent(50);
        spil.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        spil.addSpace(chance);

        ArrayList testarr = new ArrayList();
        testarr.add(go); testarr.add(p); testarr.add(chance);

        assertEquals(testarr, spil.getSpaces());
    }

    @Test
    void getUtilites() {
    }

    @Test
    void getRealestates() {
    }

    @Test
    void setSpaces() {
        //test is performed for getSpaces.
    }

    @Test
    void addSpace() {
        //test is performed for getSpaces.
    }

    @Test
    void getCardDeck() {
        //test is performed for setCardDeck.
    }

    /**
     * Test if a card can be drawn from the deck.
     * @author s180557
     * @return
     */

    @Test
    void drawCardFromDeck() {

        Game spil = new Game();

        Space go = new Space();
        go.setName("Go");
        spil.addSpace(go);

        Property p = new Property();
        p.setName("Rødovrevej");
        p.setCost(1200);
        p.setRent(50);
        spil.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        spil.addSpace(chance);

        List<Card> cards = new ArrayList<Card>();

        CardMove move = new CardMove();
        move.setTarget(spil.getSpaces().get(2));
        move.setText("Move to Allégade!");
        cards.add(move);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");
        cards.add(tax);

        CardReceiveMoneyFromBank b = new CardReceiveMoneyFromBank();
        b.setText("You receive 100$ from the bank.");
        b.setAmount(100);
        cards.add(b);
        spil.setCardDeck(cards);

        int lengde = spil.getCardDeck().size();
        Card kort = spil.drawCardFromDeck();

        //Tester at kortet er de forventede, at stakken er én mindre og at den nye top er som forventet.

        assertEquals(kort, move);
        assertEquals(lengde-1, spil.getCardDeck().size());
        assertEquals(tax, spil.getCardDeck().iterator().next());

    }

    /**
     * Test if a card can be returned to the deck.
     * @author s180557
     * @return
     */

    @Test
    void returnCardToDeck() {

        Game spil = new Game();

        Space go = new Space();
        go.setName("Go");
        spil.addSpace(go);

        Property p = new Property();
        p.setName("Rødovrevej");
        p.setCost(1200);
        p.setRent(50);
        spil.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        spil.addSpace(chance);

        List<Card> cards = new ArrayList<Card>();

        CardMove move = new CardMove();
        move.setTarget(spil.getSpaces().get(2));
        move.setText("Move to Allégade!");
        cards.add(move);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");
        cards.add(tax);

        CardReceiveMoneyFromBank b = new CardReceiveMoneyFromBank();
        b.setText("You receive 100$ from the bank.");
        b.setAmount(100);

        spil.setCardDeck(cards);

        int lengde = spil.getCardDeck().size();
        spil.returnCardToDeck(b);

        Card bunden = null;
        for (Card bund : spil.getCardDeck()) {
            bunden = bund;
        }

        //Tester at stakken er blevet én større og at den nederste kort er det forventede.

        assertEquals(lengde+1, spil.getCardDeck().size());
        assertEquals(b, bunden);

    }

    /**
     * Test if a card deck can be instansiated.
     * @author s180557
     * @return
     */

    @Test
    void setCardDeck() {

        Game spil = new Game();

        Space go = new Space();
        go.setName("Go");
        spil.addSpace(go);

        Property p = new Property();
        p.setName("Rødovrevej");
        p.setCost(1200);
        p.setRent(50);
        spil.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        spil.addSpace(chance);

        List<Card> cards = new ArrayList<Card>();

        CardMove move = new CardMove();
        move.setTarget(spil.getSpaces().get(2));
        move.setText("Move to Allégade!");
        cards.add(move);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");
        cards.add(tax);

        CardReceiveMoneyFromBank b = new CardReceiveMoneyFromBank();
        b.setText("You receive 100$ from the bank.");
        b.setAmount(100);
        cards.add(b);
        spil.setCardDeck(cards);

        ArrayList testarr = new ArrayList();
        testarr.add(move); testarr.add(tax); testarr.add(b);

        assertEquals(testarr, spil.getCardDeck());

    }

    /**
     * Test by iteration if the deck can be shuffled.
     * @author s180557
     * @return
     */

    @Test
    void shuffleCardDeck() {

        //Game is set up

        Game spil = new Game();

        Space go = new Space();
        go.setName("Go");
        spil.addSpace(go);

        Property p = new Property();
        p.setName("Rødovrevej");
        p.setCost(1200);
        p.setRent(50);
        spil.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        spil.addSpace(chance);

        List<Card> cards = new ArrayList<Card>();
        List<Card> cards2 = new ArrayList<Card>();

        CardMove move = new CardMove();
        move.setTarget(spil.getSpaces().get(2));
        move.setText("Move to Allégade!");
        cards.add(move);
        cards2.add(move);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");
        cards.add(tax);
        cards2.add(tax);

        CardReceiveMoneyFromBank b = new CardReceiveMoneyFromBank();
        b.setText("You receive 100$ from the bank.");
        b.setAmount(100);
        cards.add(b);
        cards2.add(b);
        spil.setCardDeck(cards);

        //Deck is shuffled five times. If it is not equal in one of those times, deck is being shuffled.

        boolean isShuffled = false;

        for (int i = 0; i < 3; i++) {
            spil.shuffleCardDeck();
            if (!cards2.equals(spil.getCardDeck())) {
                isShuffled = true;
            }
        }

        assertEquals(true, isShuffled);

    }

    /**
     * Test if player list can be set and fetched.
     * @author s180557
     * @return
     */

    @Test
    void getPlayers() {

        Game spil = new Game();
        Player spiller1 = new Player();
        Player spiller2 = new Player();
        Player spiller3 = new Player();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        spil.addPlayer(spiller3);

        ArrayList testarr = new ArrayList();
        testarr.add(spiller1); testarr.add(spiller2); testarr.add(spiller3);

        assertEquals(testarr, spil.getPlayers());

    }

    @Test
    void setPlayers() {
        //test is performed for getPlayers.
    }

    /**
     * Test if a player can be added.
     * @author s180557
     * @return
     */

    @Test
    void addPlayer() {

        Game spil = new Game();
        Player spiller1 = new Player();
        spil.addPlayer(spiller1);

        assertEquals(spiller1, spil.getPlayers().iterator().next());

    }

    /**
     * Test if current player can be fetched.
     * @author s180557
     * @return
     */

    @Test
    void getCurrentPlayer() {

        Game spil = new Game();
        Player spiller1 = new Player();
        Player spiller2 = new Player();
        Player spiller3 = new Player();
        spil.addPlayer(spiller1);
        spil.addPlayer(spiller2);
        spil.addPlayer(spiller3);

        assertEquals(spiller1, spil.getCurrentPlayer());

        spil.setCurrentPlayer(spiller2);

        assertEquals(spiller2, spil.getCurrentPlayer());


    }

    @Test
    void setCurrentPlayer() {
        //test is performed for getCurrentPlayers.
    }

    @Test
    void getPassStartbonus() {
    }
}