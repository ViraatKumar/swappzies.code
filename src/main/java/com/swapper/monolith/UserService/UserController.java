package com.swapper.monolith.UserService;

import com.swapper.monolith.security.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String user() {
        UserDetails user =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.format("Hi i am a user - %s - roles - %s", user.getUsername(),user.getAuthorities().toString());
    }
}
