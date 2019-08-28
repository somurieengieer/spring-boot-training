package com.sample.infrastracture.configuration;

import com.sample.application.service.SampleUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SampleUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自前のuserDetailsServiceを使用するために当メソッドをオーバーライドする。
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .authorizeRequests()
                .antMatchers("/**")
                .authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // パスワードをハッシュ化するアルゴリズムをBeanとして一元管理する。
        // 特に要件がない場合はBCryptPasswordEncoderが無難。（他にはSHA-256アルゴリズムの選択肢などがある）
        return new BCryptPasswordEncoder();
    }
}
