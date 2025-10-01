package com.swapper.monolith.dto;

import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDTO {
    private Long id;
    String username;
    String password;
    String email;
    String phoneNo;
    Set<Roles> role;
    public static  UserDTO from(User user) {
       return UserDTO.builder()
               .id(user.getId())
               .username(user.getUsername())
               .password(user.getPassword())
               .email(user.getEmail())
               .phoneNo(user.getPhoneNo())
               .role(user.getRoles())
               .build();
    }
}
