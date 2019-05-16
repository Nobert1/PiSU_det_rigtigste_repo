package monopoly.mini.model.exceptions;

import monopoly.mini.model.Player;

/**
 * An exceptions that indicates that the last player before the winner
 * has gone broke which means the game is over.
 *
 */


public class GameEndedException extends Exception {



    public GameEndedException() {
//Is the gui message here or in the catch clause?
        super("Everyone except is broke");

    }


}