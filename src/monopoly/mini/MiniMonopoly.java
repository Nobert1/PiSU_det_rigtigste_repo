package monopoly.mini;

import monopoly.mini.controller.GameController;
import monopoly.mini.database.dal.DALException;
import monopoly.mini.model.*;
import monopoly.mini.model.cards.CardMove;
import monopoly.mini.model.cards.CardReceiveMoneyFromBank;
import monopoly.mini.model.cards.PayTax;
import monopoly.mini.model.properties.Colors;
import monopoly.mini.model.properties.RealEstate;
import monopoly.mini.model.properties.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for setting up and running a (Mini-)Monoploy game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class MiniMonopoly {

    /**
     * Creates the initial static situation of a Monopoly game. Note
     * that the players are not created here, and the chance cards
     * are not shuffled here.
     *
     * @return the initial game board and (not shuffled) deck of chance cards
     */
    @SuppressWarnings("Duplicates")
    public static Game createGame() {

        // Create the initial Game set up (note that, in this simple
        // setup, we use only 11 spaces). Note also that this setup
        // could actually be loaded from a file or database instead
        // of creating it programmatically. This will be discussed
        // later in this course.
        Game game = new Game();
        return game;
    }
    public static void createSpaces(Game game) {

//TODO: input mortgage values
        int i = 0;
        int j = 0;

        Space go = new Space();
        go.setName("Go");
        game.addSpace(go);

        RealEstate p = new RealEstate();
        p.setName("Rødovrevej");
        p.setCost(60);
        p.setRent(2);
        p.setMortgageValue(30);
        p.setHousePrice(50);
        p.setColor(Colors.getcolor(Colors.LIGHTBLUE));
        p.setHousePrice(50);
        p.setRents(10,30,90,160,250);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        Chance chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Hvidovrevej");
        p.setCost(60);
        p.setRent(4);
        p.setMortgageValue(30);
        p.setHousePrice(50);
        p.setColor(Colors.getcolor(Colors.LIGHTBLUE));
        p.setRents(20,60,180,320,450);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        Tax t = new Tax();
        t.setName("Pay tax (10% on Cash)");
        game.addSpace(t);

        Utility s = new Utility();
        s.setName("Øresund");
        s.setCost(200);
        s.setRent(20);
        s.setColor(Colors.getcolor(Colors.SHIPPINGWHITE));
        Utility.getShippingLine().add(s);
        s.setPropertyid(j++);
        game.addSpace(s);


        p = new RealEstate();
        p.setName("Roskildevej");
        p.setCost(100);
        p.setRent(6);
        p.setHousePrice(50);
        p.setColor(Colors.getcolor(Colors.LIGHTRED));
        p.setRents(30,90,270,400,550);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Valby Langgade");
        p.setCost(100);
        p.setRent(6);
        p.setColor(Colors.getcolor(Colors.LIGHTRED));
        p.setRents(30,90,270,400,550);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Allégade");
        p.setCost(120);
        p.setRent(8);
        p.setHousePrice(50);
        p.setColor(Colors.getcolor(Colors.LIGHTRED));
        p.setRents(40,100,300,450,600);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        Space prison = new Space();
        prison.setName("Prison");
        game.addSpace(prison);

        p = new RealEstate();
        p.setName("Frederiksberg Allé");
        p.setCost(140);
        p.setRent(10);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREEN));
        p.setRents(50,150,450,625,750);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        s = new Utility();
        s.setName("Coca-Cola Tapperi");
        s.setCost(300);
        s.setRent(30);
        s.setColor(Colors.getcolor(Colors.DARKGREEN));
        Utility.getBreweries().add(s);
        s.setPropertyid(j++);
        game.addSpace(s);

        p = new RealEstate();
        p.setName("Bülowsvej");
        p.setCost(140);
        p.setRent(10);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREEN));
        p.setRents(50,150,450,625,750);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Gl. Kongevej"); // TODO Inkonsistens med GUI'en (pris 140, 160)
        p.setCost(160);
        p.setRent(12);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREEN));
        p.setRents(60,180,500,700,900);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        s = new Utility();
        s.setName("D.F.D.S");
        s.setCost(200);
        s.setRent(20);
        s.setColor(Colors.getcolor(Colors.SHIPPINGWHITE));
        Utility.getShippingLine().add(s);
        s.setPropertyid(j++);
        game.addSpace(s);

        p = new RealEstate();
        p.setName("Bernstorffsvej");
        p.setCost(180);
        p.setRent(14);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREY));
        p.setRents(70,200,550,750,950);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Hellerupvej");
        p.setCost(180);
        p.setRent(14);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREY));
        p.setRents(70,200,550,750,950);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Strandvejen"); //TODO inkonsistens
        p.setCost(200);
        p.setRent(16);
        p.setHousePrice(100);
        p.setColor(Colors.getcolor(Colors.GREY));
        p.setRents(80,220,600,800,1000);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        Space helle = new Space();
        helle.setName("helle");
        game.addSpace(helle);

        p = new RealEstate();
        p.setName("Trianglen");
        p.setCost(220);
        p.setRent(18);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.RED));
        p.setRents(90,250,700,875,1050);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Østerbrogade");
        p.setCost(220);
        p.setRent(18);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.RED));
        p.setRents(90,250,700,875,1050);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Grønningen");
        p.setCost(240);
        p.setRent(20);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.RED));
        p.setRents(100,300,750,925,1100);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        s = new Utility();
        s.setName("ship");
        s.setCost(2000);
        s.setRent(250);
        s.setColor(Colors.getcolor(Colors.SHIPPINGWHITE));
        s.setPropertyid(j++);
        Utility.getShippingLine().add(s);

        game.addSpace(s);

        p = new RealEstate();
        p.setName("Bredgade");
        p.setCost(260);
        p.setRent(22);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.WHITE));
        p.setRents(110,330,800,975,1150);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Kgs. Nytrov");
        p.setCost(260);
        p.setRent(22);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.WHITE));
        p.setRents(110,330,800,975,1150);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        s = new Utility();
        s.setName("Carlsberg");
        s.setCost(200);
        s.setRent(20);
        s.setColor(Colors.getcolor(Colors.DARKGREEN));
        Utility.getBreweries().add(s);
        s.setPropertyid(j++);
        game.addSpace(s);

        p = new RealEstate();
        p.setName("Østergade");
        p.setCost(280);
        p.setRent(24);
        p.setHousePrice(150);
        p.setColor(Colors.getcolor(Colors.WHITE));
        p.setRents(120,360,850,1025,1200);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        Space gotoprison = new Space();
        gotoprison.setName("Go to prison");
        game.addSpace(gotoprison);

        p = new RealEstate();
        p.setName("Amagertorv");
        p.setCost(300);
        p.setRent(26);
        p.setHousePrice(200);
        p.setColor(Colors.getcolor(Colors.YELLOW));
        p.setRents(130,390,900,1100,1275);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        p = new RealEstate();
        p.setName("Vimmelskaftet");
        p.setCost(300);
        p.setRent(26);
        p.setHousePrice(200);
        p.setColor(Colors.getcolor(Colors.YELLOW));
        p.setRents(130,390,900,1100,1275);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Nygade");
        p.setCost(320);
        p.setRent(28);
        p.setHousePrice(200);
        p.setColor(Colors.getcolor(Colors.YELLOW));
        p.setRents(150,450,1000,1200,1400);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        s = new Utility();
        s.setName("Bornholm færgen");
        s.setCost(200);
        s.setRent(20);
        s.setColor(Colors.getcolor(Colors.SHIPPINGWHITE));
        Utility.getShippingLine().add(s);
        s.setPropertyid(j++);
        game.addSpace(s);

        chance = new Chance();
        chance.setName("Chance");
        game.addSpace(chance);

        p = new RealEstate();
        p.setName("Frederiksberggade");
        p.setCost(350);
        p.setRent(35);
        p.setHousePrice(200);
        p.setColor(Colors.getcolor(Colors.PURPLE));
        p.setRents(175,500,100,1300,1500);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        t = new Tax();
        t.setName("extraordinær skat, betal 1000");
        game.addSpace(t);

        p = new RealEstate();
        p.setName("Rådhuspladsen");
        p.setCost(400);
        p.setRent(50);
        p.setHousePrice(200);
        p.setColor(Colors.getcolor(Colors.PURPLE));
        p.setRents(200,600,1400,1700,2000);
        RealEstate.insertintoColorMap(p);
        p.setPropertid(i++);
        game.addSpace(p);

        if (p.getColor().equals(Colors.LIGHTBLUE)||(p.getColor().equals(Colors.LIGHTRED))){
            p.setHousePrice(50);
        }
        if (p.getColor().equals(Colors.GREEN)||(p.getColor().equals(Colors.GREY))){
            p.setHousePrice(100);
        }
        if (p.getColor().equals(Colors.RED)||(p.getColor().equals(Colors.WHITE))){
            p.setHousePrice(150);
        }
        if (p.getColor().equals(Colors.YELLOW)||(p.getColor().equals(Colors.PURPLE))){
            p.setHousePrice(200);
        }


        List<Card> cards = new ArrayList<Card>();

        CardMove move = new CardMove();
        move.setTarget(game.getSpaces().get(9));
        move.setText("Move to Allégade!");
        cards.add(move);

        PayTax tax = new PayTax();
        tax.setText("Pay 10% income tax!");
        cards.add(tax);

        CardReceiveMoneyFromBank b = new CardReceiveMoneyFromBank();
        b.setText("You receive 100$ from the bank.");
        b.setAmount(100);
        cards.add(b);
        game.setCardDeck(cards);

    }


    /**
     * This method will be called before the game is started to create
     * the participating players.
     */
	/*
	public static void createPlayers(Game game) {
		// TODO the players should eventually be created interactively or be loaded from a database
        int i = 1;
        Player p = new Player();
		p.setName("Player 1");
		p.setCurrentPosition(game.getSpaces().get(0));
		p.setColor(Color.RED);
		p.setPlayerID(i++);
		game.addPlayer(p);
		p = new Player();
		p.setName("Player 2");
		p.setCurrentPosition(game.getSpaces().get(0));
		p.setColor(Color.YELLOW);
        p.setPlayerID(i++);
		game.addPlayer(p);
		p = new Player();
		p.setName("Player 3");
		p.setCurrentPosition(game.getSpaces().get(0));
		p.setColor(Color.GREEN);
        p.setPlayerID(i++);
		game.addPlayer(p);
	}
	*/

    /**
     * The main method which creates a game, shuffles the chance
     * cards, creates players, and then starts the game. Note
     * that, eventually, the game could be loaded from a database.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Game game = createGame();
        GameController controller = new GameController(game);
        controller.initializeGUI();
        try {
            createSpaces(game);
            controller.databaseinteraction();
            game.shuffleCardDeck();

            controller.play();
        } catch (DALException e) {
            e.getMessage();
        }
    }
}