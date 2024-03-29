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

        // H2コンソールを表示するためには以下2つの設定が必要（やらないとH2コンソールログイン後に403エラーになる）
        // 1.CSRFをオフにする
        // http.csrf().disable();
        // 2.通常はクリックジャッキング等の対策のために他サイトのフレームを呼び出さないようにしている。H2コンソール画面を表示するためにframeOptions().disable()にする
        // http.headers().frameOptions().disable();
        //   以下のような書き方でもOK  allow same origin to frame our site to support iframe SockJS
        // http.headers().frameOptions().sameOrigin()

        http
            .authorizeRequests() // 認可の設定を定義する
                .antMatchers("/admin/**", "/h2-console/**").hasRole("ADMIN") // ADMINロールのみ/admin/**のURLが有効
//                .antMatchers("/admin/**").hasRole("ADMIN") // ADMINロールのみ/admin/**のURLが有効
//                .antMatchers("/h2-console/**").hasRole("ADMIN") // ADMINロールのみH2コンソール画面を有効にする
                // anyRequest()は認可設定の最後に書く。先に.anyRequest().authenticated()を書くと認可制限が効かずどこでもアクセスできてしまうので注意
                .anyRequest() // 全てのリクエストは
                .authenticated() // 認証が必要

                .and() // 設定を続ける場合は.and()でbuilderパターン形式で実装が可能

            .formLogin() // ログインページの設定を定義する
                .loginPage("/login") // ログインページのURL
//                .usernameParameter("username3") // ログインformのユーザー名がusername以外の場合は設定が必要
//                .passwordParameter("pass_field") // ログインformのパスワードのnameがpassword以外の場合は設定が必要
                .permitAll() // ログインページへのアクセス権限を全員に付与
                .and()
            .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .and()
            .headers()
                .frameOptions().sameOrigin()
                .and()
            .logout()
//                .logoutSuccessUrl("/login?logoutSuccess") // デフォルトでは/login?logoutに遷移する。変更したい場合は先の通りに設定する
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // パスワードをハッシュ化するアルゴリズムをBeanとして一元管理する。
        // 特に要件がない場合はBCryptPasswordEncoderが無難。（他にはSHA-256アルゴリズムの選択肢などがある）
        // BCryptは16バイトの乱数をソルトとして使用し、2^10回ストレッチングを行う。
        return new BCryptPasswordEncoder();
    }
}
