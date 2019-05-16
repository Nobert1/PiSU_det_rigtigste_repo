package monopoly.mini.controller;

import monopoly.mini.model.Game;
import monopoly.mini.model.Player;

import java.awt.*;
import java.util.ArrayList;

public class PlayerController {

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
        colourList.add("Magneta");
        colourList.add("Green");

        int i = 1;
        int j = 0;
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
                    c = Color.BLUE;
                    break;
                case "Red":
                    c = Color.RED;
                    break;
                case "White":
                    c = Color.WHITE;
                    break;
                case "Yellow":
                    c = Color.YELLOW;
                    break;
                case "Magneta":
                    c = Color.magenta;
                    break;
                case "Green":
                    c = Color.GREEN;
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
}
