package com.jacob.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.DTO.ProfileDTO;
import com.jacob.backend.data.Model.Friend;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.FriendRepositoryInterface;
import com.jacob.backend.responses.NotFoundException;

@Service
public class FriendService {

    @Autowired
    private FriendRepositoryInterface friendRepo;

    @Autowired
    private UserService userService;

    public List<ProfileDTO> findById(UUID id) {
        List<Friend> friends = friendRepo.getById(id);
        List<ProfileDTO> friendProfiles = new ArrayList<ProfileDTO>();
        for (Friend f : friends) {
            if (!f.getPending()) {
                UUID friendUUID = f.getUserAId().equals(id) ? f.getUserBId() : f.getUserAId();
                User friend = userService.findById(friendUUID);
                ProfileDTO friendProfile = new ProfileDTO(0, friend.getUsername(), friend.getEmail());
                friendProfiles.add(friendProfile);
            }
        }
        return friendProfiles;
    }

    public List<ProfileDTO> findByUsername(String username) {
        User u = userService.findByUsername(username);
        if (u == null) {
            throw new NotFoundException("User", "Username: " + username);
        }
        return findById(u.getId());
    }

    public Friend findByIds(UUID userId, UUID friendId) {
        return friendRepo.getByIds(userId, friendId);
    }

    public void addByIds(UUID userId, UUID friendId) {
        Friend friendship = friendRepo.getByIds(userId, friendId);
        if (friendship == null) {
            Friend newFriend = new Friend();
            newFriend.setPending(true);
            newFriend.setUserAId(userId);
            newFriend.setUserBId(friendId);
            friendRepo.save(newFriend);
        } else if (friendship.getUserBId().equals(userId)) {
            friendship.setPending(false);
            friendRepo.update(friendship);
        }
    }

    public void addByUsernames(String userUsername, String friendUsername) {
        User user = userService.findByUsername(userUsername);
        if (user == null) {
            throw new NotFoundException("User", "Username: " + userUsername);
        }
        User friend = userService.findByUsername(friendUsername);
        if (friend == null) {
            throw new NotFoundException("User", "Username: " + friendUsername);
        }
        addByIds(user.getId(), friend.getId());
    }

    public void deleteByIds(UUID userId, UUID friendId) {
        Friend friendship = findByIds(userId, friendId);
        if (friendship != null) {
            friendRepo.delete(friendship);
        }
    }

    public void deleteByUsernames(String userUsername, String friendUsername) {
        User user = userService.findByUsername(userUsername);
        if (user == null) {
            throw new NotFoundException("User", "Username: " + userUsername);
        }
        User friend = userService.findByUsername(friendUsername);
        if (friend == null) {
            throw new NotFoundException("User", "Username: " + friendUsername);
        }
        deleteByIds(user.getId(), friend.getId());
    }
}
