package com.swapper.monolith.service;

import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.model.User;
import com.swapper.monolith.dto.SignUpRequest;
import com.swapper.monolith.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService  {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public String addUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setUsername(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return "User Added Successfully";
    }
    public UserDTO getUser(String username){
        User user = userRepository.findByUsername(username).orElseGet(null);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return UserDTO.from(user);
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(Role.USER.name()) // or map from user.getRole()
                .build();
    }
}
