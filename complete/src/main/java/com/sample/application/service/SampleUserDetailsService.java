package com.sample.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SampleUserDetailsService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User.UserBuilder builder =
                User.withUsername(username)
                .password(passwordEncoder.encode("password"));
        if (username.equals("admin")) {
            builder.roles("USER", "ADMIN");
        } else {
            builder.roles("USER");
        }
        // authoritiesかroleに何かしら設定しないとログインできない？上記に加えてWebSecurityConfigにhasRole設定をすることで動作した

        return builder.build();
    }
}
