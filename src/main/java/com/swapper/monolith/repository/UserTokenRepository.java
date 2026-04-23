package com.swapper.monolith.repository;

import com.swapper.monolith.model.User;
import com.swapper.monolith.model.UserToken;
import com.swapper.monolith.model.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {

    Optional<UserToken> findByTokenAndType(String hashedToken, TokenType type);
    Optional<UserToken> findByToken(String token);
    @Transactional
    void deleteByUserAndType(User user, TokenType type);

    @Transactional
    void deleteByTokenAndType(String token, TokenType type);
}
