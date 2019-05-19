package monopoly.mini.controller;

import monopoly.mini.model.Game;
import monopoly.mini.model.Player;
import monopoly.mini.model.Space;
import monopoly.mini.model.exceptions.PlayerBrokeException;
import monopoly.mini.model.properties.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * player controller which has all the methods for creating and moving players.
 * @author s175124
 */


public class PlayerController {


    /**
     * Method which creates players interactively
     * @author s175124
     * @param game
     * @param gameController
     */

    public void createPlayers(Game game, GameController gameController) {
        int players = 0;
        do {
            players = gameController.getGui().getUserInteger(" How many players? Max 6 players.\n Remember youngest goes first!");
            if (players > 1 && players < 7) {
                break;
            } else {
                gameController.getGui().showMessage("Not a valid number");
            }
        }while(players < 2 || players > 6);

        ArrayList<String> iconList = new ArrayList<>();
        iconList.add("CAR");
        iconList.add("Racecar");
        iconList.add("UFO");
        iconList.add("Tractor");
        ArrayList<String> colourList = new ArrayList<>();
        colourList.add("Blue");
        colourList.add("Red");
        colourList.add("White");
        colourList.add("Yellow");
        colourList.add("Magenta");
        colourList.add("Green");

        int i = 1;
        do{
            Player p = new Player();
            String name = "";
            while(name.equals("")) {
                name = gameController.getGui().getUserString("What would you like your name to be?");
            }
            String[] iconArr = gameController.arrayConverterString(iconList);
            String[] colourArr = gameController.arrayConverterString(colourList);
            String icon = gameController.getGui().getUserButtonPressed("Which icon would you like to have?", iconArr);
            String colour = gameController.getGui().getUserButtonPressed("Which colour would you like?", colourArr);
            Color c;
            switch (colour){
                case "Blue":
                    c = Colors.getcolor(Colors.LIGHTBLUE);
                    break;
                case "Red":
                    c = Colors.getcolor(Colors.RED);
                    break;
                case "White":
                    c = Colors.getcolor(Colors.WHITE);
                    break;
                case "Yellow":
                    c = Colors.getcolor(Colors.YELLOW);
                    break;
                case "Magenta":
                    c = Colors.getcolor(Colors.MAGENTA);
                    break;
                case "Green":
                    c = Colors.getcolor(Colors.GREEN);
                    break;
                default:
                    c = Color.GREEN;

            }


            p.setName(name);
            p.setCurrentPosition(game.getSpaces().get(0));
            p.setColor(c);
            p.setIcon(icon.toUpperCase());
            p.setPlayerID(i++);
            game.addPlayer(p);

            iconList.remove(name);
            colourList.remove(colour);

        }while(i <= players);


    }

    /**
     * This method implements a activity of a single move of the given player.
     * It throws a {@link dk.dtu.compute.se.pisd.monopoly.mini.model.exceptions.PlayerBrokeException}
     * if the player goes broke in this move. Note that this is still a very
     * basic implementation of the move of a player; many aspects are still
     * missing.
     *
     * @param player the player making the move
     * @throws PlayerBrokeException if the player goes broke during the move
     */
    public void makeMove(Player player, GameController gameController) throws PlayerBrokeException {
        boolean castDouble;
        int doublesCount = 0;
        boolean isNotInJail = true;

        do {
            int die1 = (int) (1 + 6 * Math.random());
            int die2 = (int) (1 + 6 * Math.random());

            gameController.setDiecount(die1, die2);
            castDouble = (die1 == die2);
            gameController.getGui().setDice(die1, die2);

            if (player.isInPrison() && castDouble) {
                player.setInPrison(false);
                gameController.getGui().showMessage("Player " + player.getName() + " leaves prison now since he cast a double!");
            } else if (player.isInPrison()) {
                gameController.getGui().showMessage("Player " + player.getName() + " stays in prison since he did not cast a double!");
            }

            if (castDouble) {
                doublesCount++;
                if (doublesCount > 2) {
                    gameController.getGui().showMessage("Player " + player.getName() + " has cast the third double and goes to jail!");
                    gotoJail(player,gameController);
                    return;
                }
            }
            if (!player.isInPrison()) {
                // make the actual move by computing the new position and then
                // executing the action moving the player to that space
                int pos = player.getCurrentPosition().getIndex();
                List<Space> spaces = gameController.getGame().getSpaces();
                int newPos = (pos + die1 + die2) % spaces.size();
                Space space = spaces.get(newPos);
                moveToSpace(player, space,gameController);
                checkForGoToJail(player,gameController);

                if (player.getCurrentPosition().getIndex() == 10) {
                    isNotInJail = false;
                } else isNotInJail = true;

                if (castDouble && isNotInJail) {
                    gameController.getGui().showMessage("Player " + player.getName() + " cast a double and makes another move.");
                }
            }
        } while (castDouble && isNotInJail);
    }

    public void checkForGoToJail (Player player, GameController gameController) throws PlayerBrokeException {
        int pos = player.getCurrentPosition().getIndex();
        if (pos == 30) {
            gotoJail(player,gameController);
        }
    }

    /**
     * The method implements the action of a player going directly to jail.
     *
     * @param player the player going to jail
     */
    public void gotoJail(Player player, GameController gameController) {
        // Field #10 is in the default game board of Monopoly the field
        // representing the prison.
        player.setCurrentPosition(gameController.getGame().getSpaces().get(10));
        player.setInPrison(true);
    }

    /**
     * This method implements the activity of moving the player to the new position,
     * including all actions associated with moving the player to the new position.
     *
     * @param player the moved player
     * @param space  the space to which the player moves
     * @throws PlayerBrokeException when the player goes broke doing the action on that space
     */
    public void moveToSpace(Player player, Space space, GameController gameController) throws PlayerBrokeException {
        int posOld = player.getCurrentPosition().getIndex();
        player.setCurrentPosition(space);

        if (posOld > player.getCurrentPosition().getIndex() && !player.isNotPassingGo()) {
            // Note that this assumes that the game has more than 12 spaces here!
            gameController.getGui().showMessage("Player " + player.getName() + " receives " + gameController.getGame().getPassstartbonus() + " for passing Go!");
            gameController.getPaymentController().paymentFromBank(player, gameController.getGame().getPassstartbonus());
        }
        gameController.getGui().showMessage("Player " + player.getName() + " arrives at " + space.getIndex() + ": " + space.getName() + ".");

        // Execute the action associated with the respective space. Note
        // that this is delegated to the field, which implements this action
        space.doAction(gameController, player);
    }

}
