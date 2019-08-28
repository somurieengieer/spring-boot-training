package com.sample.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
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

        User.UserBuilder builder = User.withUsername(username)
                .password(passwordEncoder.encode("password"))
                .authorities(AuthorityUtils.createAuthorityList("ADMIN_USER"));
        // authoritiesは何かしら設定しないとログインできないので注意

        return builder.build();
    }
}
