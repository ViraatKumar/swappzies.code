package com.swapper.monolith.service;

import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import com.swapper.monolith.dto.SignUpRequest;
import com.swapper.monolith.repository.RoleRepository;
import com.swapper.monolith.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService  {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


    public String addUser(SignUpRequest signUpRequest) {

        User user = new User();
        user.setUsername(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Set<Roles> userRoles = setUserRoles(Set.of(Role.USER));
        user.setRoles(userRoles);
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
    private Set<Roles> setUserRoles(Set<Role> requesedRoles) {
        Set<Roles> userRoles = new HashSet<>();
        roleRepository.findAll().forEach(role -> {
            try {
                if (requesedRoles.contains(role.getRole())) {
                    userRoles.add(role);
                } else {
                    throw new RoleNotFoundException(role.getRole().name());
                }
            }
            catch (RoleNotFoundException e) {
                LoggerFactory.getLogger(UserService.class).error(e.getMessage());
            }
        });

        return userRoles;
    }
}
