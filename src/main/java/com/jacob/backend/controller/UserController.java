package com.jacob.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jacob.backend.data.DTO.*;
import com.jacob.backend.data.Model.Session;
import com.jacob.backend.data.Model.Status;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.service.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.responses.exceptions.UnauthorizedException;

/**
 * Controller containing endpoints to get data for the current User
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {

    /**
     * Service containing Session related logic
     */
    @Autowired
    private SessionService sessionService;

    /**
     * Service containing User related logic
     */
    @Autowired
    private UserService userService;

    /**
     * Service containing Friend related logic
     */
    @Autowired
    private FriendService friendService;

    /**
     * Gets the usernames of all users
     * 
     * @param sessionId the session id of the current user
     * @return a list of all users' usernames
     */
    @GetMapping
    public ResponseEntity<String> getUsers(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {

            // ensure the user at least has a session currently
            sessionService.getUsernameById(sessionId);

            // get all users
            List<User> users = userService.findAll();

            List<String> usernames = new ArrayList<String>();

            for (User user : users) {
                usernames.add(user.getUsername());
            }

            // return all users
            return ResponseEntity.ok().body(JSONResponses.toJson("users", usernames));

        } catch (UnauthorizedException e) {

            // catch unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic exception - return bad request
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }

    }

    /**
     * Add or Confirm the Friend relation between the current User and the given
     * User
     * 
     * @param sessionId         the id of the Session to identify the current User
     * @param friendUsernameDTO the DTO with the Username to identify the other User
     * @return Success message if the operation was successful, else 4XX
     */
    @PostMapping("/friends")
    public ResponseEntity<String> addFriend(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO friendUsernameDTO) {
        try {

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Update
            friendService.addByUsernames(username, friendUsernameDTO.getUsername());

            // return successful
            return ResponseEntity.ok().body(JSONResponses.success());

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Removes the Friend relation between the current User and the given User
     * 
     * @param sessionId         the id of the Session to identify the current User
     * @param friendUsernameDTO the DTO with the Username of the other User
     * @return a success message if the operation was successful, else 4XX
     */
    @DeleteMapping("/friends")
    public ResponseEntity<String> removeFriends(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO friendUsernameDTO) {
        try {

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Update
            friendService.deleteByUsernames(username, friendUsernameDTO.getUsername());

            // return successful
            return ResponseEntity.ok().body(JSONResponses.success());

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Get all Friends of the current User
     * 
     * @param sessionId the id of the Session to identify the current User
     * @param pending   optional flag that determines whether or not to include
     *                  pending Friend relations
     * @return list of Friends if the operation was successful, else 4XX
     */
    @GetMapping("/friends")
    public ResponseEntity<String> getFriends(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) Boolean pending) {
        try {

            // get the username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Get
            List<FriendDTO> friends = friendService.findByUsername(username, pending != null && pending);

            // return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("friends", friends));

        } catch (UnauthorizedException e) {

            // catch Unauthroized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Updates the Credentials of the current User
     * 
     * @param sessionId the id of the Session to identify the current User
     * @param creds     the new Credentials to associate with the current User
     * @return a success message if the operation was successful, else 4XX
     */
    @PutMapping("/profile")
    public ResponseEntity<String> editProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO creds) {
        try {

            // get the username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            Session session = new Session();
            session.setUsername(creds.getUsername());

            // Update the Session to store the new Username
            sessionService.update(UUID.fromString(sessionId), session);

            // perform the Update
            userService.update(username, creds);

            // return successful
            return ResponseEntity.ok().body(JSONResponses.success());

        } catch (UnauthorizedException e) {

            // catch Unaothorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Gets the Profile of the current User
     * 
     * @param sessionId the id of the Session to identify the current User
     * @return the current Users Profile if the operation was successful, else 4XX
     */
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Get
            ProfileDTO profile = userService.getProfile(username);

            // build the result object and return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("profile", profile));

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    @PutMapping("/session")
    public ResponseEntity<String> setOnlineStatus(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false, defaultValue = "") String currentGameId) {
        try {

            Session session = new Session();

            if (currentGameId.length() > 0)
                session.setCurrentGameId(UUID.fromString(currentGameId));
            session.setOnlineStatus(status);

            sessionService.update(UUID.fromString(sessionId), session);

            return ResponseEntity.ok().body(JSONResponses.success());

        } catch (UnauthorizedException e) {

            // catch unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic exception - return bad request
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

}
