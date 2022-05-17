package com.example.demo.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@RequiredArgsConstructor
@Table(name = "Token")
@Entity
public class TokenEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity userEntity;

    @Builder
    public TokenEntity(String refreshToken, UserEntity userEntity) {
        System.out.println("Create Token Entity");
        this.refreshToken = refreshToken;
        this.userEntity = userEntity;
    }

    public void refreshTokenUpdate(String refreshToken) {
        System.out.println("Refresh TOKEN Update");
        this.refreshToken = refreshToken;
    }
}
