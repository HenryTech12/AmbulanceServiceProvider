package org.flexisaf.intern_showcase.configurations;

import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.mappers.UserMapper;
import org.flexisaf.intern_showcase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(
                userRepository.findByEmail(username).
                        map(data ->
                                UserDTO.builder()
                                        .email(data.getEmail())
                                        .password(data.getPassword())
                                        .role(data.getRole())
                                        .build())
                        .orElse(new UserDTO())
        );
    }
}
