package com.ecsite.security;

import com.ecsite.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http.formLogin().loginPage("/").permitAll();
        http.formLogin().loginPage("/toLogin").loginProcessingUrl("/login").defaultSuccessUrl("/login/success", true)// ログイン成功URL
                .failureUrl("/toLogin?error")// ログイン失敗URL
                .usernameParameter("email").passwordParameter("password").permitAll();

        http.authorizeRequests() // 認証が必要となるURLを設定します
                // .antMatchers("/**").permitAll() // /以下のURLも認証不要
                // ログインしないと閲覧できないページ
                .antMatchers("/shoppingcart/toOrder", "/logout", "/shoppingcart/order","/shoppingcart-history").authenticated()
                // .antMatchers("/", "/toInsert", "/insert", "/login").permitAll()
                .anyRequest().permitAll();
        // ログアウト処理の設定
        http.logout()
                // ログアウト処理のURL
                // .logoutUrl("/logout")
                // ログアウト成功時の遷移先URL
                .logoutSuccessUrl("/logout/success").permitAll();
        // ログアウト時に削除するクッキー名
        // .deleteCookies("JSESSIONID")
        // ログアウト時のセッション破棄を有効化
        // .invalidateHttpSession(true)
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img_coffee/**");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private LoginService loginService;

    @Autowired
    void configureAuthenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder());
    }
}