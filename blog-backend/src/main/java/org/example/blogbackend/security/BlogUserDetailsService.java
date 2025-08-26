package org.example.blogbackend.security;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.repository.UserRepository;
import org.example.blogbackend.model.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class BlogUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public BlogUserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));
        return new BlogUserDetails(user);
    }

}
