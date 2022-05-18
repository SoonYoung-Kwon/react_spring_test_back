package com.example.demo.config;

import com.example.demo.entitiy.UserEntity;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenUtils {

    private final String ACCESS_KEY = "ACCESS";
    private final String REFRESH_KEY = "REFRESH";

    private final int ACCESS_TIME = 10 * 1000;
    private final int REFRESH_TIME = 24 * 60 * 60 * 1000;

    public String generateAccessToken(UserEntity userEntity) {
        System.out.println("Generate Access Token");

        return Jwts.builder()
                .setSubject("JWT") // Name
                .setHeader(createHeader()) // Header
                .setClaims(createPayload(userEntity)) // Payload
                .setExpiration(createExpireDate(ACCESS_TIME)) // Time
                .signWith(SignatureAlgorithm.HS256, createHashKey(ACCESS_KEY)) // Hash
                .compact(); // Generate
    }

    public String generateRefreshToken(UserEntity userEntity) {
        System.out.println("Generate Refresh Token");

        return Jwts.builder()
                .setSubject("JWT") // Name
                .setHeader(createHeader()) // Header
                .setClaims(createPayload(userEntity)) // Payload
                .setExpiration(createExpireDate(REFRESH_TIME)) // Time
                .signWith(SignatureAlgorithm.HS256, createHashKey(REFRESH_KEY)) // Hash
                .compact(); // Generate
    }

    public String isValidToken(String token, String type) {
        System.out.println("Is Valid " + type + " Token");

        try{
            Claims accessClaims = getClaimsToken(token, type);
            System.out.println("Access User ID : " + accessClaims.get("userId"));
            System.out.println("Access Time Limit : " + accessClaims.getExpiration());
            return "true";
        } catch (ExpiredJwtException exception) {
            System.out.println("Expired User");
            return "expired";
        } catch (JwtException exception) {
            System.out.println("Token Tampered");
            return "false";
        } catch (NullPointerException exception) {
            System.out.println("Access Token is null");
            return "false";
        }
    }

    private Map<String, Object> createHeader() {
        System.out.println("Create Header");
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT"); // Type
        header.put("alg", "HS256"); // Hashing
        header.put("regDate", System.currentTimeMillis()); // Register Time

        System.out.println("Register Date : " + new Date(System.currentTimeMillis()));

        return header;
    }

    private Map<String, Object> createPayload(UserEntity userEntity) {
        System.out.println("Create Claims");
        Map<String, Object> payload = new HashMap<>();

        payload.put("userId", userEntity.getUserId());

        return payload;
    }

    private Date createExpireDate(long expireDate) {
        System.out.println("Create Expire Date");
        long currentTime = System.currentTimeMillis();

        return new Date(currentTime + expireDate);
    }

    private Key createHashKey(String key) {
        System.out.println("Create Hash Key");
        byte[] secretKey = DatatypeConverter.parseBase64Binary(key);

        return new SecretKeySpec(secretKey, SignatureAlgorithm.HS256.getJcaName());
    }

    private Claims getClaimsToken(String token, String type) {
        System.out.println("Get Claims in Token");

        if (type.equals("ACCESS")) {
            System.out.println("1");
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(ACCESS_KEY)) // parse Binary with REFRESH_KEY
                    .parseClaimsJws(token)
                    .getBody();
        }
        else {
            System.out.println("2");
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(REFRESH_KEY)) // parse Binary with REFRESH_KEY
                    .parseClaimsJws(token)
                    .getBody();
        }
    }
}
