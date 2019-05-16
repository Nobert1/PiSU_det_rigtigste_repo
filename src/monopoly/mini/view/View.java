package monopoly.mini.view;

import designpatterns.Observer;
import designpatterns.Subject;
import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Property;
import monopoly.mini.model.Space;
import monopoly.mini.model.properties.RealEstate;
import gui_fields.*;
import gui_fields.GUI_Car.Pattern;
import gui_main.GUI;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a view on the Monopoly game based
 * on the original Matador GUI; it serves as a kind of
 * adapter to the Matador GUI. This class realizes the
 * MVC-principle on top of the Matador GUI. In particular,
 * the view implements an observer of the model in the
 * sense of the MVC-principle, which updates the GUI when
 * the state of the game (model) changes.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class View implements Observer {

    private PlayerPanel playerPanel;
    private Game game;
    private GUI gui;

    private Map<Player,GUI_Player> player2GuiPlayer = new HashMap<Player,GUI_Player>();
    private Map<Player,Integer> player2position = new HashMap<Player,Integer>();
    private Map<Space,GUI_Field> space2GuiField = new HashMap<Space,GUI_Field>();
    private Map<Player,PlayerPanel> playerPanelMap = new HashMap<Player,PlayerPanel>();


    private boolean disposed = false;

    /**
     * Constructor for the view of a game based on a game and an already
     * running Matador GUI.
     *
     * @param game the game
     * @param gui the GUI
     */
    public View(Game game, GUI gui, PlayerPanel playerPanel) {
        this.game = game;
        this.gui = gui;
        this.playerPanel = playerPanel;
    }



    public void createFields() {
        GUI_Field[] guiFields = gui.getFields();
        int i = 0;
        for (Space space: game.getSpaces()) {
            // TODO, here we assume that the games fields fit to the GUI's fields;
            // the GUI fields should actually be created according to the game's
            // fields
            space2GuiField.put(space, guiFields[i++]);
            space.attach(this);
            update(space);
            //Her skal der evt være en eller anden form for update metode, så som updatespace eller update player
            // TODO we should also register with the properties as observer; but
            // the current version does not update anything for the spaces, so we do not
            // register the view as an observer for now
        }
        // create the players in the GUI
    }

    public void createplayers() {
        for (Player player : game.getPlayers()) {
            GUI_Car car = new GUI_Car(player.getColor(), Color.black, GUI_Car.Type.getTypeFromString(player.getIcon()), Pattern.FILL);
            GUI_Player guiPlayer = new GUI_Player(player.getName(), player.getBalance(), car);
            PlayerPanel panel = new PlayerPanel(game, player);
            player2GuiPlayer.put(player, guiPlayer);
            playerPanelMap.put(player, panel);
            gui.addPlayer(guiPlayer);
            panel.setVisible(true);
            player2position.put(player, player.getCurrentPosition().getIndex());
            // register this view with the player as an observer, in order to update the
            // player's state in the GUI
            player.attach(this);
            updatePlayer(player);
        }
    }/**
     public void loadplayers() {
     for (Player player : game.getPlayers()) {
     GUI_Car car = new GUI_Car(player.getColor(), Color.black, Type.CAR, Pattern.FILL);
     GUI_Player gui_player = new GUI_Player(player.getName(), player.getBalance(), car);
     PlayerPanel panel = new PlayerPanel(game, player);
     playerPanelMap.put(player, panel);
     player2GuiPlayer.put(player, gui_player);
     gui.addPlayer(gui_player);
     player2position.put(player, player.getCurrentPosition().getIndex());
     player.attach(this);
     update(player);
     }
     }
     **/

    @Override
    public void update(Subject subject) {
        if (!disposed) {
            if (subject instanceof Player) {
                updatePlayer((Player) subject);
            }
            if (subject instanceof Property) {
                updateProperty((Property) subject);
            }
            // TODO update other subjects in the GUI
            //      in particular properties (sold, houses, ...)

        }
    }

    /**
     * Method author s185031 - Gustav Emil Nobert
     * @param property
     */

    private void updateProperty(Property property) {
        GUI_Ownable field = (GUI_Ownable) this.space2GuiField.get(property);

        if (property.isOwned()){
            field.setBorder(property.getOwner().getColor());
            field.setOwnerName(property.getOwner().getName());
        }
        if (!property.isOwned()) {
            field.setBorder(null);
            field.setOwnerName(null);
        }

        if (property instanceof RealEstate) {
            RealEstate realestate = (RealEstate) property;
            GUI_Street estatefield = (GUI_Street) field;
            estatefield.setHouses(realestate.getHouses());
            if (realestate.isHotel()) {
                estatefield.setHouses(0);
                estatefield.setHotel(true);

            }
        }
    }

    /**
     * This method updates a player's state in the GUI. Right now, this
     * concerns the players position and balance only. But, this should
     * also include other information (being i prison, available cards,
     * ...)
     *
     * @param player the player who's state is to be updated
     */
    private void updatePlayer(Player player) {
        GUI_Player guiPlayer = this.player2GuiPlayer.get(player);
        PlayerPanel panel = this.playerPanelMap.get(player);

        if (guiPlayer != null) {
            guiPlayer.setBalance(player.getBalance());

            GUI_Field[] guiFields = gui.getFields();
            Integer oldPosition = player2position.get(player);
            if (oldPosition != null && oldPosition < guiFields.length) {
                guiFields[oldPosition].setCar(guiPlayer, false);
            }
            int pos = player.getCurrentPosition().getIndex();
            if (pos == 30) {
                player2position.put(player,pos);
                guiFields[pos].setCar(guiPlayer,true);
            } else if (pos < guiFields.length) {
                player2position.put(player,pos);
                guiFields[pos].setCar(guiPlayer, true);
            }

            String name = player.getName();
            if (player.isBroke()) {
            } else if (player.isInPrison()) {
                guiPlayer.setName(player.getName() + " (in prison)");
            }
            if (!name.equals(guiPlayer.getName())) {
                guiPlayer.setName(name);
            }
            panel.update(player);
        }
    }

    public void dispose() {
        if (!disposed) {
            disposed = true;
            for (Player player: game.getPlayers()) {
                // unregister from the player as observer
                player.detach(this);
            }
        }
    }


}