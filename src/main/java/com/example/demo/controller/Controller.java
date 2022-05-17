package com.example.demo.controller;

import com.example.demo.entitiy.TokenEntity;
import com.example.demo.entitiy.UserEntity;
import com.example.demo.model.ClientRequest;
import com.example.demo.service.ClientService;
import jdk.nashorn.internal.runtime.ECMAException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class Controller {

    private final ClientService clientService;

    @PostMapping("/user/signUp")
    public ResponseEntity signUp(@RequestBody ClientRequest clientRequest) {
        System.out.println("Controller - Sign Up");

        if(clientService.findByUserId(clientRequest.getUserId()).isPresent()){
            return ResponseEntity.badRequest().build(); // 500 Error
        }
        else {
            return ResponseEntity.ok(clientService.signUp(clientRequest));
        }
    }

    @PostMapping("/user/signIn")
    public ResponseEntity signIn(@RequestBody ClientRequest clientRequest) throws ECMAException {
        System.out.println("Controller - Sign In");

        return ResponseEntity.ok().body(clientService.signIn(clientRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<List<UserEntity>> info() {
        System.out.println("Controller - Info");


        return ResponseEntity.ok().body(clientService.info());
    }

    @GetMapping("/token/info")
    public ResponseEntity<List<TokenEntity>> tokenInfo() {
        System.out.println("Controller - Token Info");

        return ResponseEntity.ok().body(clientService.tokenInfo());
    }

    @GetMapping("/message")
    public String message() {
        return "message";
    }
}
