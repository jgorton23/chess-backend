package com.jacob.backend.data;

import java.sql.Time;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue
    private UUID id;

    private String[] board;

    private String moves;

    private int turn;

    private Time whiteTime;

    private Time blackTime;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    public Game() {

    }

    public String[] getBoard() {
        return board;
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Time getWhiteTime() {
        return whiteTime;
    }

    public void setWhiteTime(Time time) {
        whiteTime = time;
    }

    public Time getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(Time time) {
        blackTime = time;
    }

    public UUID getWhitePlayerId() {
        return whitePlayerId;
    }

    public void setWhitePlayerId(UUID id) {
        whitePlayerId = id;
    }

    public UUID getBlackPlayerId() {
        return blackPlayerId;
    }

    public void setBlackPlayerId(UUID id) {
        blackPlayerId = id;
    }
}
