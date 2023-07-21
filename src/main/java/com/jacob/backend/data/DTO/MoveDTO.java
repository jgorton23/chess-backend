package com.jacob.backend.data.DTO;

public class MoveDTO {

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

    /**
     * Creates a new MoveDTO object
     */
    public MoveDTO() {
    }

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
     * convert the move to SAN format
     */
    @Override
    public String toString() {
        return piece + (char) (startSquare[0] + 'a') + startSquare[1] + (char) (destSquare[0] + 'a') + destSquare[1];
    }
}
