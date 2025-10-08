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
        private Long id;
        private String name;
        private String mobile;
        private String email;
        private String password;
        private List<GrantedAuthority> authorities;

        public UserPrincipal(Long id, String name, String mobile, String email, String password, List<GrantedAuthority> authorities) {
            this.id = id;
            this.name = name;
            this.mobile = mobile;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
        }

        public static UserPrincipal create(User user) {
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return new UserPrincipal(
                    user.getId(),
                    user.getName(),
                    user.getMobileNumber(),
                    user.getEmail(),
                    user.getPassword(),
                    authorities);
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
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