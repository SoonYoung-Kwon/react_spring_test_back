package com.example.demo.service;

import com.example.demo.config.TokenUtils;
import com.example.demo.entitiy.TokenEntity;
import com.example.demo.entitiy.UserEntity;
import com.example.demo.model.ClientRequest;
import com.example.demo.model.ServerResponse;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // 생성자 주입
public class ClientService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final TokenUtils tokenUtils;

    public Optional<UserEntity> findByUserId(String userId) {
        System.out.println("Find User ID");

        return userRepository.findByUserId(userId);
    }

    public ServerResponse signUp(ClientRequest clientRequest) {
        System.out.println("Sign Up");

        UserEntity userEntity =
                userRepository.save(
                        UserEntity.builder()
                                .userId(clientRequest.getUserId())
                                .userPw(clientRequest.getUserPw())
                                .build());

        //String accessToken = tokenUtils.generateAccessToken(userEntity);
        //String refreshToken = tokenUtils.generateRefreshToken(userEntity);

        tokenRepository.save(TokenEntity.builder().refreshToken(null).userEntity(userEntity).build());

        return ServerResponse.builder().accessToken(null).refreshToken(null).msg("Sign Up Success").build();
    }

    public ServerResponse signIn(ClientRequest clientRequest) {
        System.out.println("Sign In");

        UserEntity userEntity =
                userRepository.findByUserId(clientRequest.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User ID Not Found"));

        TokenEntity tokenEntity =
                tokenRepository.findByUserEntityId(userEntity.getId()) // UserRepository TokenRepository equals primary key (id)
                        .orElseThrow(() -> new IllegalArgumentException("Token Not Found"));

        String accessToken = tokenUtils.generateAccessToken(userEntity);
        String refreshToken = tokenUtils.generateRefreshToken(userEntity);
        //tokenEntity.getRefreshToken();

        if(tokenUtils.isValidToken(refreshToken, "REFRESH")) {
            return ServerResponse.builder().accessToken(accessToken).refreshToken(refreshToken).msg("Sign In Success").build();
        }
        else {
            refreshToken = tokenUtils.generateRefreshToken((userEntity));
            tokenEntity.refreshTokenUpdate(refreshToken);

            return ServerResponse.builder().accessToken(accessToken).refreshToken(refreshToken).msg("Direct Refrems").build();
        }
    }

    public List<UserEntity> info() {
        System.out.println("Info");
        return userRepository.findAll();
    }

    public List<TokenEntity> tokenInfo() {
        System.out.println("Info");
        return tokenRepository.findAll();
    }
}
