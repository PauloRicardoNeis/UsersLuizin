package com.usersluizin.userservice.service;

import com.usersluizin.userservice.domain.Role;
import com.usersluizin.userservice.domain.User;
import com.usersluizin.userservice.dto.CreateUserRequest;
import com.usersluizin.userservice.dto.UpdateUserRequest;
import com.usersluizin.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        validateEmailAndCpfUniqueness(request.getEmail(), request.getCpf(), null);
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCpf(request.getCpf());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> listUsers(boolean includeInactive) {
        if (includeInactive) {
            return userRepository.findAll();
        }
        return userRepository.findAll().stream().filter(User::isActive).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public User updateUser(UUID userId, UpdateUserRequest request) {
        User user = getUser(userId);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getCpf() != null) {
            validateEmailAndCpfUniqueness(null, request.getCpf(), userId);
            user.setCpf(request.getCpf());
        }
        if (request.getEmail() != null) {
            validateEmailAndCpfUniqueness(request.getEmail(), null, userId);
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User deactivateUser(UUID userId) {
        User user = getUser(userId);
        user.setActive(false);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Credenciais inválidas"));
    }

    private void validateEmailAndCpfUniqueness(String email, String cpf, UUID currentUserId) {
        if (email != null) {
            Optional<User> existing = userRepository.findByEmail(email);
            if (existing.isPresent() && (currentUserId == null || !existing.get().getId().equals(currentUserId))) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
        }
        if (cpf != null) {
            Optional<User> existing = userRepository.findByCpf(cpf);
            if (existing.isPresent() && (currentUserId == null || !existing.get().getId().equals(currentUserId))) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
        }
    }
}
