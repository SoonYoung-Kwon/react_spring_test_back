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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    public ServerResponse signUp(ClientRequest clientRequest) { // Sing Up 은 Repository save 만 할 수 있도록 변경
        System.out.println("Sign Up");

        UserEntity userEntity =
                userRepository.save(
                        UserEntity.builder()
                                .userId(clientRequest.getUserId())
                                .userPw(clientRequest.getUserPw())
                                .build());

        tokenRepository.save(TokenEntity.builder().refreshToken(null).userEntity(userEntity).build());

        return ServerResponse.builder().accessToken(null).refreshToken(null).msg("Sign Up Success").build();
    }

    @Transactional // 없으면 refreshTokenUpdate 결과 없음
    public ServerResponse signIn(ClientRequest clientRequest) { // sign In 은 처음으로 Access Token 과 Refresh Token 을 발급하도록 설정
        System.out.println("Sign In");

        String accessToken = null;
        String refreshToken = null;

        System.out.println("client id : " + clientRequest.getUserId());

        UserEntity userEntity =
                userRepository.findByUserId(clientRequest.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User ID Not Found"));

        if(userEntity.getUserPw() != clientRequest.getUserPw()){
            return ServerResponse.builder().accessToken(null).refreshToken(null).msg("Password Different").build();
        }

        TokenEntity tokenEntity =
                tokenRepository.findByUserEntityId(userEntity.getId()) // UserRepository TokenRepository equals primary key (id)
                        .orElseThrow(() -> new IllegalArgumentException("Token Not Found"));

        accessToken = tokenUtils.generateAccessToken(userEntity);
        refreshToken = tokenUtils.generateRefreshToken(userEntity);

        tokenEntity.refreshTokenUpdate(refreshToken);

        return ServerResponse.builder().accessToken(accessToken).refreshToken(refreshToken).msg("New Sign In").build();


        /*if(tokenUtils.isValidToken(refreshToken, "REFRESH")) {
            return ServerResponse.builder().accessToken(accessToken).refreshToken(refreshToken).msg("Sign In Success").build();
        }
        else {
            refreshToken = tokenUtils.generateRefreshToken((userEntity));
            tokenEntity.refreshTokenUpdate(refreshToken);

            return ServerResponse.builder().accessToken(accessToken).refreshToken(refreshToken).msg("Direct Refresh").build();
        }*/
    }

    @Transactional
    public ServerResponse renewAccessToken(ClientRequest clientRequest, HttpServletRequest request) {
        System.out.println("Renew Access Token");

        switch (tokenUtils.isValidToken(request.getHeader("REFRESH_TOKEN"), "REFRESH")) {
            case "true":
                UserEntity userEntity =
                        userRepository.findByUserId(clientRequest.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User ID Not Found"));
                TokenEntity tokenEntity =
                        tokenRepository.findByUserEntityId(userEntity.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Token Not Found"));
                String accessToken = tokenUtils.generateAccessToken(userEntity);
                return ServerResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(tokenEntity.getRefreshToken())
                        .msg("Renew Access Token").build();
            default:
                return ServerResponse.builder()
                        .accessToken(null)
                        .refreshToken(null)
                        .msg("Please Sign In Again").build();
        }
    }

    public List<UserEntity> info(ClientRequest clientRequest, HttpServletRequest request) { // token 검증이 필요한 곳은 interceptor 로 항상 넘어가지 못하게 설정할 것
        System.out.println("Info");

        switch (tokenUtils.isValidToken(request.getHeader("ACCESS_TOKEN"), "ACCESS")) {
            case "true":
                return userRepository.findAll();
            case "expired":
                List<UserEntity> t = new ArrayList<UserEntity>();
                return t;
            default:
                return null;
        }
    }

    public List<TokenEntity> tokenInfo() {
        System.out.println("Info");
        return tokenRepository.findAll();
    }


}
