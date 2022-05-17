package com.example.demo.repository;

import com.example.demo.entitiy.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByUserEntityId(Long userId);
}
