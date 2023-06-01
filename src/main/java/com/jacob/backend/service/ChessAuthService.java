package com.jacob.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Credentials;
import com.jacob.backend.repository.ChessAuthRepository;

@Service
public class ChessAuthService {

    @Autowired
    private ChessAuthRepository authRepo;

    public String login(Credentials cred) {
        
        return authRepo.login();
    }

    public String register() {
        return "register service";
    }

}
