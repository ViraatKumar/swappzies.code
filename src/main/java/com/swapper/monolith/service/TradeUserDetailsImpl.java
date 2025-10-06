package com.swapper.monolith.service;

import com.swapper.monolith.model.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TradeUserDetailsImpl implements UserDetails {
    @Serial
    long serialVersionUID = 1L;
    long id;
    String userId;
    String username;
    String password;
    Collection<? extends GrantedAuthority> authorities;

    public static TradeUserDetailsImpl build(User user) {
        List<GrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());
        return new TradeUserDetailsImpl(
                user.getId(),
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }
    public TradeUserDetailsImpl(long id, String userId, String username, String password,
                                Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
