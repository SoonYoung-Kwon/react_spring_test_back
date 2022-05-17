package com.example.demo.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@RequiredArgsConstructor
@Table(name = "user")
@Entity
public class UserEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String userId;
    private String userPw;

    @Builder
    public UserEntity(String userId, String userPw) {
        System.out.println("Create User Entity");
        this.userId = userId;
        this.userPw = userPw;
    }
}