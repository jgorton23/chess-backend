package com.jacob.backend.data.DTO;

public class MoveDTO {

    // region variables

    /**
     * x, y coordinates of the initial square
     */
    private int[] startSquare;

    /**
     * x, y coordinates of the destination square
     */
    private int[] destSquare;

    /**
     * the piece that is moving: e.g. 'N', 'n', 'K', 'k', etc
     */
    private String piece;

    /**
     * if the piece is a pawn, signifies the piece that the pawn will be promoted to
     * after the move
     */
    private String promotion;

    /**
     * if the move puts the opponent in check
     */
    private boolean isCheck;

    /**
     * if the move puts the opponent in mate
     */
    private boolean isMate;

    /**
     * if the move puts the opponnent in stalemate
     */
    private boolean isStalemate;

    /**
     * if the move captures an opposing piece
     */
    private boolean isCapture;

    /**
     * the amount of time the move took in miliseconds
     */
    private int miliseconds;

    /**
     * the username of the player making the move
     */
    private String playerUsername;

    // endregion

    /**
     * Creates a new MoveDTO object
     */
    public MoveDTO() {
    }

    // region getters/setters

    /**
     * Gets the starting square of this Move
     * 
     * @return an int array representing the x,y coordinates of the starting square
     */
    public int[] getStartSquare() {
        return startSquare;
    }

    /**
     * Sets the starting square of this Move
     * 
     * @param newStartSquare an int array representing the new x,y coordinates of
     *                       the starting square
     */
    public void setStartSquare(int[] newStartSquare) {
        startSquare = newStartSquare;
    }

    /**
     * Gets the destination square of this Move
     * 
     * @return an int array representing the x,y coordinates of the destination
     *         square
     */
    public int[] getDestSquare() {
        return destSquare;
    }

    /**
     * Sets the destination square of this Move
     * 
     * @param newDestSquare an int array representing the new x,y coordinates of the
     *                      destination square
     */
    public void setDestSquare(int[] newDestSquare) {
        destSquare = newDestSquare;
    }

    /**
     * Gets the piece to move
     * 
     * @return a String representing the piece that will move
     */
    public String getPiece() {
        return piece;
    }

    /**
     * Sets the piece to move
     * 
     * @param newPiece a String representing the new piece that will move
     */
    public void setPiece(String newPiece) {
        piece = newPiece;
    }

    /**
     * If the piece is a pawn, and the move will result in a promotion,
     * this gets the piece that the pawn will be promoted to after moving
     * 
     * @return the new Piece as a string
     */
    public String getPromotion() {
        return promotion;
    }

    /**
     * Sets the piece that the current piece will be promoted to after moving. Only
     * relevant for pawn moves resulting in promotions
     * 
     * @param promotion the piece to be promoted to
     */
    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    /**
     * Gets whether or not the move captures an opposing piece
     * 
     * @return True if the move was a capture, else False
     */
    public boolean getIsCapture() {
        return isCapture;
    }

    /**
     * Sets whether or not the move is a capture
     * 
     * @param isCapture if the move is a capture or not
     */
    public void setIsCapture(boolean isCapture) {
        this.isCapture = isCapture;
    }

    /**
     * Gets whether or not the move checks the opponent
     * 
     * @return True if the move puts the opponent in check, else False
     */
    public boolean getIsCheck() {
        return isCheck;
    }

    /**
     * Sets if the move checks the opponent or not
     * 
     * @param isCheck if the move checks the opponent
     */
    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    /**
     * Gets if the move check mates the opponent
     * 
     * @return True if the move is Checkmate, else false
     */
    public boolean getIsMate() {
        return isMate;
    }

    /**
     * Sets if the move check mates the opponent or not
     * 
     * @param isMate if the move is checkmate
     */
    public void setIsMate(boolean isMate) {
        this.isMate = isMate;
    }

    /**
     * Gets if the move stalemates the opponent
     * 
     * @return True if the move is Stalemate, else false
     */
    public boolean getIsStalemate() {
        return isStalemate;
    }

    /**
     * Sets if the move stalemates the opponent or not
     * 
     * @param isStalemate if the move is stalemate
     */
    public void setIsStalemate(boolean isStalemate) {
        this.isStalemate = isStalemate;
    }

    /**
     * Gets how long the move took in miliseconds
     * 
     * @return how many miliseconds the move took
     */
    public int getMiliseconds() {
        return miliseconds;
    }

    /**
     * Sets how many miliseconds the move took
     * 
     * @param miliseconds how many miliseconds the move took
     */
    public void setMiliseconds(int miliseconds) {
        this.miliseconds = miliseconds;
    }

    /**
     * Gets the username of the player who made this move
     * 
     * @return the username of the player who made the move
     */
    public String getPlayerUsername() {
        return playerUsername;
    }

    /**
     * Sets the username of the player who made this move
     * 
     * @param username the username of the player who made the move
     */
    public void setPlayerUsername(String username) {
        playerUsername = username;
    }

    // endregion

    /**
     * convert the move to SAN format
     */
    @Override
    public String toString() {
        String move = "";
        String start = "" + (char) (startSquare[0] + 'a') + Math.abs(startSquare[1] - 8);
        String end = "" + (char) (destSquare[0] + 'a') + Math.abs(destSquare[1] - 8);

        move += piece;
        move += start;
        if (isCapture) {
            move += "x";
        }
        move += end;
        if (isMate) {
            move += "#";
        } else if (isCheck) {
            move += "+";
        } else if (isStalemate) {
            move += "$";
        }
        return move;
    }

    public static MoveDTO fromString(String moveString) {

        MoveDTO move = new MoveDTO();

        move.setIsCapture(moveString.contains("x"));
        moveString = moveString.replace("x", "");

        move.setIsMate(moveString.contains("#"));
        moveString = moveString.replace("#", "");

        move.setIsCheck(moveString.contains("+"));
        moveString = moveString.replace("+", "");

        move.setIsStalemate(moveString.contains("$"));
        moveString = moveString.replace("$", "");

        move.setPiece(moveString.substring(0, 1));
        moveString = moveString.substring(1);

        int[] startSquare = new int[] { (int) moveString.charAt(0) - (int) 'a',
                Math.abs(Integer.parseInt(moveString.substring(1, 2)) - 8) };
        move.setStartSquare(startSquare);
        moveString = moveString.substring(2);

        int[] destSquare = new int[] { (int) moveString.charAt(0) - (int) 'a',
                Math.abs(Integer.parseInt(moveString.substring(1, 2)) - 8) };
        move.setDestSquare(destSquare);
        moveString = moveString.substring(2);

        return move;

    }
}
