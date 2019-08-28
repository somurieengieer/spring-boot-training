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
        // ログイン画面で入力されたユーザー名・パスワードは以下処理に渡される。
        // 今回の場合、BCryptでエンコードされた後にuserDetailsServiceのloadUserByUserNameの返り値と照合される
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // 認可の設定を定義する
                .antMatchers("/admin/**").hasRole("ADMIN") // ADMINロールのみ/admin/**のURLが有効
                // anyRequest()は認可設定の最後に書く。先に.anyRequest().authenticated()を書くと認可制限が効かずどこでもアクセスできてしまうので注意
                .anyRequest() // 全てのリクエストは
                .authenticated() // 認証が必要

                .and() // 設定を続ける場合は.and()でbuilderパターン形式で実装が可能

            .formLogin() // ログインページの設定を定義する
                .loginPage("/login") // ログインページのURL
//                .usernameParameter("username3") // ログインformのユーザー名がusername以外の場合は設定が必要
//                .passwordParameter("pass_field") // ログインformのパスワードのnameがpassword以外の場合は設定が必要
                .permitAll(); // ログインページへのアクセス権限を全員に付与
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // パスワードをハッシュ化するアルゴリズムをBeanとして一元管理する。
        // 特に要件がない場合はBCryptPasswordEncoderが無難。（他にはSHA-256アルゴリズムの選択肢などがある）
        // BCryptは16バイトの乱数をソルトとして使用し、2^10回ストレッチングを行う。
        return new BCryptPasswordEncoder();
    }
}
