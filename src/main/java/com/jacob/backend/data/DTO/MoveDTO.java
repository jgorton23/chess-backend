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
     * if the move puts the opponent in check
     */
    private boolean isCheck;

    /**
     * if the move puts the opponent in mate
     */
    private boolean isMate;

    /**
     * if the move captures an opposing piece
     */
    private boolean isCapture;

    /**
     * the amount of time the move took in miliseconds
     */
    private int miliseconds;

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

    // endregion

    /**
     * convert the move to SAN format
     */
    @Override
    public String toString() {
        return piece + (char) (startSquare[0] + 'a') + Math.abs(startSquare[1] - 8) + (char) (destSquare[0] + 'a')
                + Math.abs(destSquare[1] - 8);
    }
}
