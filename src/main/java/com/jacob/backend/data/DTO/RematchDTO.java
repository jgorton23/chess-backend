package com.jacob.backend.data.DTO;

public class RematchDTO {

    private boolean whitePlayerConfirmed;

    private boolean blackPlayerConfirmed;

    private String newGameId;

    public RematchDTO() {
    }

    public boolean getWhitePlayerConfirmed() {
        return whitePlayerConfirmed;
    }

    public void setWhitePlayerConfirmed(boolean confirmed) {
        whitePlayerConfirmed = confirmed;
    }

    public boolean getBlackPlayerConfirmed() {
        return blackPlayerConfirmed;
    }

    public void setBlachPlayerConfirmed(boolean confirmed) {
        blackPlayerConfirmed = confirmed;
    }

    public String getNewGameId() {
        return newGameId;
    }

    public void setNewGameId(String gameId) {
        newGameId = gameId;
    }

}
