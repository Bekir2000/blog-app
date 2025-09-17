package org.example.blogbackend.common.security;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Builder
public class BlogUserDetails implements UserDetails {

    @Getter
    private final UUID userId;
    private final String email;
    private final String password;


    public static BlogUserDetails fromDb(UUID id, String email, String password, List<String> roles) {
        return new BlogUserDetails(id, email, password);
    }

    public static BlogUserDetails fromJwt(UUID id, String email, List<String> roles) {
        return new BlogUserDetails(id, email, null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
