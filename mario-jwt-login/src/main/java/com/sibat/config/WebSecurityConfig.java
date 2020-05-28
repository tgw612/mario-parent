package com.sibat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
//@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AnyUserDetailsService anyUserDetailsService;

    @Autowired
    JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 匹配 "/" 路径，不需要权限即可访问
     * 匹配 "/user" 及其以下所有路径，都需要 "USER" 权限
     * 登录地址为 "/login"，登录成功默认跳转到页面 "/user"
     * 退出登录的地址为 "/logout"，退出成功后跳转到页面 "/login"
     * 默认启用 CSRF
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/user/change_password").hasRole("ADMIN")
                .antMatchers("/user/add_user").hasRole("USER")
                .antMatchers("/police/").hasRole("USER")
                .anyRequest().authenticated()
//                 .antMatchers("/change_password").access("hasRole(\"USER\") and hasRole(\"ADMIN\")")
//                .and().httpBasic()
                //  .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").deleteCookies()
                .and().csrf().disable();
        http
                .rememberMe().alwaysRemember(true);
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/captcha").hasRole("USER")
//                .antMatchers("/delete_user").hasRole("USER")
//
////                .and()
////                .formLogin().loginPage("/login").defaultSuccessUrl("/user")
////                .and()
////                .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
//                .and().csrf().disable();
//    }

    /**
     * 添加 AnyUserDetailsService， 实现自定义登录校验
     */
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(anyUserDetailsService);
    }
}
