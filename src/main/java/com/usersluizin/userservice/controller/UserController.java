package com.usersluizin.userservice.controller;

import com.usersluizin.userservice.domain.Role;
import com.usersluizin.userservice.domain.User;
import com.usersluizin.userservice.dto.CreateUserRequest;
import com.usersluizin.userservice.dto.UpdateUserRequest;
import com.usersluizin.userservice.dto.UserResponse;
import com.usersluizin.userservice.mapper.UserMapper;
import com.usersluizin.userservice.security.ApplicationUserDetails;
import com.usersluizin.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    public ResponseEntity<List<UserResponse>> listUsers(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive) {
        List<User> users = userService.listUsers(includeInactive);
        return ResponseEntity.ok(users.stream().map(UserMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.getUser(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(UserMapper.toResponse(userService.createUser(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdateUserRequest request,
                                                   @AuthenticationPrincipal ApplicationUserDetails principal) {
        if (!canUpdate(principal, id, request)) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este usuário");
        }
        return ResponseEntity.ok(UserMapper.toResponse(userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INSTITUTION')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.deactivateUser(id)));
    }

    private boolean canUpdate(ApplicationUserDetails principal, UUID targetUserId, UpdateUserRequest request) {
        if (principal == null) {
            return false;
        }
        Role role = principal.getUser().getRole();
        boolean isAdminOrInstitution = role == Role.ADMIN || role == Role.INSTITUTION;
        if (isAdminOrInstitution) {
            return true;
        }
        return principal.getUser().getId().equals(targetUserId)
                && (request.getRole() == null || request.getRole() == Role.USER)
                && (request.getActive() == null || request.getActive());
    }
}
