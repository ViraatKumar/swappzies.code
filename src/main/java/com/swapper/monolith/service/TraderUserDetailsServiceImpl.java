package com.swapper.monolith.service;

import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TraderUserDetailsServiceImpl implements UserDetailsService {
    UserRepository userRepository;
    public TraderUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user =  userRepository.findByUsername(username)
               .orElseThrow(()-> new RuntimeException());
       return TradeUserDetailsImpl.build(user);
    }
}
