package com.swapper.monolith.dto;

import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserDTO {
    private long id;
    String userId;
    String username;
    String password;
    String email;
    String phoneNo;
    Set<Roles> role;
    public static  UserDTO from(User user) {
       return UserDTO.builder()
               .userId(user.getUserId())
               .username(user.getUsername())
               .email(user.getEmail())
               .password(user.getPassword())
               .phoneNo(user.getPhoneNo())
               .role(user.getRoles())
               .build();
    }
}
