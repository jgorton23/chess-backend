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

    public MoveDTO() {
    }

    public int[] getStartSquare() {
        return startSquare;
    }

    public void setStartSquare(int[] newStartSquare) {
        startSquare = newStartSquare;
    }

    public int[] getDestSquare() {
        return destSquare;
    }

    public void setDestSquare(int[] newDestSquare) {
        destSquare = newDestSquare;
    }

    public String getPiece() {
        return piece;
    }

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
