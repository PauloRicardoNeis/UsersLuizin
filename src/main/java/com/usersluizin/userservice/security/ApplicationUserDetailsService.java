package com.usersluizin.userservice.security;

import com.usersluizin.userservice.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public ApplicationUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new ApplicationUserDetails(userService.findByEmail(username));
        } catch (Exception ex) {
            throw new UsernameNotFoundException("Credenciais inválidas", ex);
        }
    }
}
