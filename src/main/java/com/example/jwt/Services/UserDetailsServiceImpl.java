package com.example.jwt.Services;

import com.example.jwt.Enity.Role;
import com.example.jwt.Repository.UserDetailsService;
import com.example.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         com.example.jwt.Enity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found: " + username));

        // convert from User entity -> UserDetails
        return new User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthority(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthority(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

}
