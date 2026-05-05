package com.swapper.monolith.controller;

import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.dto.PublicUserProfileResponse;
import com.swapper.monolith.dto.UpdateProfileRequest;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.dto.UserDetailsResponse;
import com.swapper.monolith.dto.GlobalRequestHandlerDto.ApiResponse;
import com.swapper.monolith.service.UserDetailsImpl;
import com.swapper.monolith.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1")
@PreAuthorize("hasAuthority('USER')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       return ResponseEntity.ok(userService.getUser(userDetails));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserDetailsResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        UserDetailsResponse updated = userService.updateProfile(request, principal);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<PublicUserProfileResponse> getUserPublicProfileResponse(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserPublicProfileResponse(authentication));
    }

}
