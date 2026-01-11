package com.swapper.monolith.service;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.EmailSignUpRequest;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.exception.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.RoleRepository;
import com.swapper.monolith.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.Internal;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService  {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


    public String addUser(EmailSignUpRequest emailSignUpRequest)  {
        User user = userRepository.findByUsernameOrEmail(emailSignUpRequest.getUsername(),emailSignUpRequest.getEmail()).orElse(null);
        if(user!=null){
            throw new DuplicatedResourceException(ApiResponses.USER_DUPLICATED_ERROR);
        }
        User newUser =  User.builder()
                .email(emailSignUpRequest.getEmail())
                .username(emailSignUpRequest.getUsername())
                .password(passwordEncoder.encode(emailSignUpRequest.getPassword()))
                .roles(setUserRoles(Set.of(Role.USER)))
                .phoneNo(null)
                .build();
        userRepository.save(newUser);
        return "User Added Successfully";
    }
    public UserDTO getUser(UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Username not found - "+userDetails.getUsername()));
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return UserDTO.from(user);
    }
    private Set<Roles> setUserRoles(Set<Role> requestedRoles) {
        Set<Roles> userRoles = new HashSet<>();
        roleRepository.findAll().forEach(role -> {
            try {
                if (requestedRoles.contains(role.getRole())) {
                    userRoles.add(role);
                } else {
                    throw new RoleNotFoundException(role.getRole().name());
                }
            }
            catch (RoleNotFoundException e) {
                LoggerFactory.getLogger(UserService.class).error(e.getMessage());
            }
        });
        if(userRoles.size() != requestedRoles.size()){
            throw new InternalServerException("some role probably doesnt exist - Requested Roles - " + requestedRoles);
        }
        return userRoles;
    }
    public ApiResponse<Boolean> checkUsername(String username){
        boolean userExists = userRepository.findByUsername(username).isPresent();
        ApiResponses apiResponse = ApiResponses.OK;
        return ApiResponse.<Boolean>builder()
                .status(apiResponse.getHttpStatus())
                .message(apiResponse.getMessage())
                .payload(userExists)
                .build();
    }

    public User findByUserId(String userId){
        User user = userRepository.findByUserId(userId).orElseThrow(
                ()-> new UsernameNotFoundException("Username not found - "+userId));
        if(user == null){
            throw new ResourceNotFoundException("User not found with user id - " + userId);
        }
        return user;
    }

}
