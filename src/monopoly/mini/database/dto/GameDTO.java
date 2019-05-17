package monopoly.mini.database.dto;

public class GameDTO implements IGameDTO {
    //Fields
    private String SaveName;
    private int GameID;

    public String getSaveName() {
        return SaveName;
    }

    public void setSaveName(String saveName) {
        SaveName = saveName;
    }

    public void setGameID(int gameID) {
        GameID = gameID;
    }

    public int getGameID() {
        return GameID;
    }
}
