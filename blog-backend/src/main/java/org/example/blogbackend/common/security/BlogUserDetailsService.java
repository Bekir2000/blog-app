package org.example.blogbackend.common.security;

import lombok.RequiredArgsConstructor;
import org.example.blogbackend.user.repository.UserRepository;
import org.example.blogbackend.user.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class BlogUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public BlogUserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));
        return BlogUserDetails.fromDb(user.getId(), user.getEmail(), user.getPassword(), null);
    }

}
