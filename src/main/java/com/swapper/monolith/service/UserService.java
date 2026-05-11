package com.swapper.monolith.service;

import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.service.ItemListingService;
import com.swapper.monolith.dto.*;
import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.RoleRepository;
import com.swapper.monolith.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService  {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ItemListingService itemListingService;


    public String addUser(EmailSignUpRequest emailSignUpRequest)  {
        User user = userRepository.findByUsernameOrEmail(emailSignUpRequest.getUsername(),emailSignUpRequest.getEmail()).orElse(null);
        if(user!=null){
            throw new DuplicatedResourceException(ApiResponses.DUPLICATED_RESOURCE);
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
    public UserDetailsResponse getUser(UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Username not found - "+userDetails.getUsername()));
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return UserDetailsResponse.from(user);
    }
    public UserDTO getCurrentAuthenticatedUser(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = userDetails.getUserId();
        User user = userRepository.findByUserId(userDetails.getUserId()).orElse(null);
        if(user == null){
            return null;
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
    public boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public UserDetailsResponse updateProfile(UpdateProfileRequest request, UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userDetails.getUserId()));
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getLat() != null) user.setLat(request.getLat());
        if (request.getLng() != null) user.setLng(request.getLng());
        try {
            return UserDetailsResponse.from(userRepository.save(user));
        }
        catch(Exception e){
            throw new InternalServerException(e.getMessage());
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public java.util.Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public PublicUserProfileResponse getUserPublicProfileResponse(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserId(userDetails.getUserId()).orElseThrow(()-> new UsernameNotFoundException("User not found: " + userDetails.getUserId()));
        return createPublicUserProfileResponse(user);
    }

    private PublicUserProfileResponse createPublicUserProfileResponse(User user) {
        String activeListingCount = "N/A";
        try {
            activeListingCount = String.valueOf(itemListingService.getUserActiveTrades(user.getUserId(), ListingState.ACTIVE));
        }
        catch(Exception exception){
            logger.error(exception.getMessage());
        }
        return PublicUserProfileResponse.builder()
                .username(user.getUsername())
                .dispalyName(user.getDisplayName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .lat(5.5)
                .lng(5.5)
                .memberSince(calculateMemberSinceValue(user))
                .tradeCount("N/A")
                .activeListingCount(activeListingCount)
                .build();
    }
    private String calculateMemberSinceValue(User user){
        Duration duration = Duration.between(user.getCreatedAt(), Instant.now());

        long days = duration.toDays();
        if (days > 0) {
            return days + " days ago";
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + " hours ago";
        }

        return duration.getSeconds() + " seconds ago";
    }

    public UserDTO findUserById(String userId){
        return UserDTO.from(userRepository.findByUserId(userId).orElseThrow(()-> new UsernameNotFoundException("User not found: " + userId)));
    }
}
