package com.example.springsecuritydemo.security;

import com.example.springsecuritydemo.auth.ApplicationUserService;
import com.example.springsecuritydemo.jwt.JwtConfig;
import com.example.springsecuritydemo.jwt.JwtTokenVerifier;
import com.example.springsecuritydemo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static com.example.springsecuritydemo.security.ApplicationUserRole.*;

/**
 * 1. BY default spring security provide - FORM Base Authentication -- you can logout ( In Memory Database) it will not get the popup in browser. [.formLogin()]
 * 2. Implement basic Auth Security using WebSecurityConfigurerAdaptor -- you can't logout ( In Memory Database) it will get the popup in browser.[.httpBasic()]
 * 3. Implement JSON Web Token(JWT)-- This is good for single authentication for Mobile,Web,etc.--
 *          +   Fast,
 *          +   Stateless,
 *          +   Used Across many services
 *          - compromised secret key
 *          - No visibility to logged in users
 *          - Token can be stolen
 */


/**
 *  sessionId - Inmemory database -- 30 mins default
 *  remember me - inmemory database -- 2 weeks default
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService, SecretKey secretKey, JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //JWT token use
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(),jwtConfig, secretKey)) //JWT token use
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey),JwtUsernameAndPasswordAuthenticationFilter.class) //JWT token verifier
                .authorizeRequests()
                .antMatchers("/","/index.html","/css/*","/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
               // .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
              //  .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
              //  .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
              //  .antMatchers("/management/api/**").hasAnyRole(ADMIN.name(),ADMINTRAINEE.name())
                .anyRequest()
                .authenticated();
             /**
              * Implemented the JWT replace of below code;
              *
                .and()
                //.httpBasic();
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/courses",true)
                .and()
                .rememberMe()
                    .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21)) //default 2 weeks
                    .key("somethingverysecured")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JESSIONID","remember-me")
                    .logoutSuccessUrl("/login"); **/
    }

    /**
     * This is used for Immemory instead of DB.
     *
     @Override
    @Bean
    protected UserDetailsService userDetailsService() {

        UserDetails userDetails = User.builder()
                .username("dasari")
                .password(passwordEncoder.encode("dasari"))
              //  .roles(STUDENT.name())
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails maryUser = User.builder()
                .username("mary")
                .password(passwordEncoder.encode("mary123"))
                //.roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails venkatUser = User.builder()
                .username("venkat")
                .password(passwordEncoder.encode("venkat123"))
                //.roles(ADMINTRAINEE.name())
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(userDetails,maryUser,venkatUser);

    }**/

    /**
     * This is used - get the users from DB.
     */

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(applicationUserService);
        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
}
