package com.emojisphere.service;

import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        User user = userRepository.findByMobileNumber(mobile)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with mobile: " + mobile));

        return UserPrincipal.create(user);
    }

    public static class UserPrincipal implements UserDetails {
        private String id;
        private String fullName;
        private String mobile;
        private String email;
        private String password;
        private List<GrantedAuthority> authorities;

        public UserPrincipal(String id, String fullName, String mobile, String email, String password, List<GrantedAuthority> authorities) {
            this.id = id;
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
        }

        public static UserPrincipal create(User user) {
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            return new UserPrincipal(
                    user.getMobileNumber(),
                    user.getFullName(),
                    user.getMobileNumber(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    authorities);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return fullName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            // Extract role from authorities (remove "ROLE_" prefix)
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
                    .findFirst()
                    .orElse("USER"); // Default to USER if no role found
        }

        @Override
        public String getUsername() {
            return mobile; // Use mobile as username for Spring Security
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public List<GrantedAuthority> getAuthorities() {
            return authorities;
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
}