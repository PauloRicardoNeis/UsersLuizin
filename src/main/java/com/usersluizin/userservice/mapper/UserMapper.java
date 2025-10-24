package com.usersluizin.userservice.mapper;

import com.usersluizin.userservice.domain.User;
import com.usersluizin.userservice.dto.UserResponse;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCpf(user.getCpf());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
