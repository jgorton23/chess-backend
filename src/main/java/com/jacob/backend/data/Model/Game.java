package com.jacob.backend.data.Model;

import java.util.UUID;

import javax.json.JsonObject;

import com.jacob.backend.data.JsonConvertible;
import com.jacob.backend.responses.JSONResponses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game implements JsonConvertible {
    @Id
    @GeneratedValue
    private UUID id;

    private String board;

    private String moves;

    private int turn;

    private int whiteTime;

    private int blackTime;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private String whitePlayerUsername;

    private String blackPlayerUsername;

    private boolean started;

    private boolean ended;

    private String winner;

    public Game() {

    }

    public String getId() {
        return id.toString();
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
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

    public int getWhiteTime() {
        return whiteTime;
    }

    public void setWhiteTime(int time) {
        whiteTime = time;
    }

    public int getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(int time) {
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

    public String getWhitePlayerUsername() {
        return whitePlayerUsername;
    }

    public void setWhitePlayerUsername(String username) {
        whitePlayerUsername = username;
    }

    public String getBlackPlayerUsername() {
        return blackPlayerUsername;
    }

    public void setBlackPlayerUsername(String username) {
        blackPlayerUsername = username;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean getStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean getEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public JsonObject toJson() {
        return JSONResponses.objectBuilder()
                .add("id", id.toString())
                .add("board", board)
                .add("moves", moves)
                .add("turn", turn)
                .add("whiteTime", whiteTime)
                .add("blackTime", blackTime)
                .add("whitePlayerUUID", whitePlayerId.toString())
                .add("blackPlayerUUID", blackPlayerId.toString())
                .add("whiteUsername", whitePlayerUsername)
                .add("blackUsername", blackPlayerUsername)
                .build();
    }
}
