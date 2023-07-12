package com.jacob.backend.data.Model;

import java.util.Date;
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

    /**
     * The UUID identifying this Game
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The current board in FEN format
     */
    private String FEN;

    /**
     * A space delimited list of moves in SAN format
     */
    private String moves;

    /**
     * A space delimited list of how many miliseconds each move took
     */
    private String moveTimes;

    /**
     * The time format of this Game. Of the format [initialMinutes/increment]:
     * e.g. '10/5', meaning both players get 10 minutes to start and the clock has a
     * 5 second increment
     */
    private String timeControl;

    /**
     * The UUID identifying the white player
     */
    private UUID whitePlayerId;

    /**
     * The UUID identifying the black player
     */
    private UUID blackPlayerId;

    /**
     * The username of the white player
     */
    private String whitePlayerUsername;

    /**
     * The username of the black player
     */
    private String blackPlayerUsername;

    /**
     * The result of the Game in the format is the number of points the white player
     * got and the number of points the black player got separated by a hyphen if
     * the Game is over. e.g. '1-0', '1/2-1/2', or '0-1'
     * 
     * If the Game is not over then it will be '*'
     */
    private String result;

    /**
     * The date which the Game was created
     */
    private Date date;

    /**
     * Created a new Game object
     */
    public Game() {
        date = new Date();
        result = "*";
    }

    // #region getters/setters

    /**
     * @return The UUID of this Game as a String
     */
    public String getId() {
        return id.toString();
    }

    /**
     * @return The FEN of this Game
     */
    public String getFEN() {
        return FEN;
    }

    /**
     * Updates the FEN of this Game
     * 
     * @param FEN the new FEN to store
     */
    public void setFEN(String FEN) {
        this.FEN = FEN;
    }

    /**
     * Gets the moves of this Game as a space delimited list of SAN formatted moves
     * 
     * @return The moves String
     */
    public String getMoves() {
        return moves;
    }

    /**
     * Updates the moves String of this Game
     * 
     * @param moves the new moves String to store
     */
    public void setMoves(String moves) {
        this.moves = moves;
    }

    /**
     * Gets a space delimited list of how long each move took in miliseconds
     * 
     * @return the list of moveTimes
     */
    public String getMoveTimes() {
        return moveTimes;
    }

    /**
     * Updates the moveTimes String of this Game
     * 
     * @param moveTimes the new moveTimes String to store
     */
    public void setMoveTimes(String moveTimes) {
        this.moveTimes = moveTimes;
    }

    /**
     * @return the time format of the Game, in the format
     *         [initialMinutes/increment].
     *         E.g. '10/5'
     */
    public String getTimeControl() {
        return timeControl;
    }

    /**
     * Updates the TimeControl of this Game
     * 
     * @param timeControl the new TimeControl to use
     */
    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    /**
     * @return the result of this Game
     */
    public String getResult() {
        return result;
    }

    /**
     * Updates the result of this Game
     * 
     * @param result the new result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the date this game was created
     */
    public Date getDate() {
        return date;
    }

    /**
     * Updates the date this game was created
     * 
     * @param date the new Date to store
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the UUID of the white player
     */
    public UUID getWhitePlayerId() {
        return whitePlayerId;
    }

    /**
     * Updates the UUID of the white player of this Game
     * 
     * @param id the new UUID of the white player
     */
    public void setWhitePlayerId(UUID id) {
        whitePlayerId = id;
    }

    /**
     * @return the UUID of the black player of this Game
     */
    public UUID getBlackPlayerId() {
        return blackPlayerId;
    }

    /**
     * Updates the UUID of the black player of this Game
     * 
     * @param id the new UUID of the black player
     */
    public void setBlackPlayerId(UUID id) {
        blackPlayerId = id;
    }

    /**
     * @return the username of the white player of this Game
     */
    public String getWhitePlayerUsername() {
        return whitePlayerUsername;
    }

    /**
     * Updates the username of the white player of this Game
     * 
     * @param username the new username of the white player
     */
    public void setWhitePlayerUsername(String username) {
        whitePlayerUsername = username;
    }

    /**
     * @return the username of the black player of this Game
     */
    public String getBlackPlayerUsername() {
        return blackPlayerUsername;
    }

    /**
     * Updates the username of the black player of this Game
     * 
     * @param username the new username of the black player
     */
    public void setBlackPlayerUsername(String username) {
        blackPlayerUsername = username;
    }

    // #endregion

    /**
     * Turns this Game object into a JsonObject
     */
    public JsonObject toJson() {
        return JSONResponses.objectBuilder()
                .add("id", id.toString())
                .add("date", date.toString())
                .add("FEN", FEN)
                .add("moves", moves)
                .add("moveTimes", moveTimes)
                .add("timeControl", timeControl)
                .add("result", result)
                .add("whitePlayerId", whitePlayerId.toString())
                .add("blackPlayerId", blackPlayerId.toString())
                .add("whitePlayerUsername", whitePlayerUsername)
                .add("blackPlayerUsername", blackPlayerUsername)
                .build();
    }
}
