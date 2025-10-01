package com.swapper.monolith.service;

import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TraderUserDetailsImpl implements UserDetailsService {
    UserRepository userRepository;
    public TraderUserDetailsImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user =  userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getUsername())
//                .password(user.getPassword())
//                .roles(Role.USER.name()) // or map from user.getRole()
//                .build();
//    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user =  userRepository.findByUsername(username)
               .orElseThrow(()-> new RuntimeException());
       return TradeUserDetailsImpl.build(user);
    }
}
