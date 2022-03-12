package com.cliff.aws.blogen.config;

import com.cliff.aws.blogen.services.security.AwsCognitoJwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This config expects an AWS Cognito ID-Token to be passed with every HTTP request that accesses a secured API.
 * The ID-token is a JSON Web Token (JWT), and should be passed in the Authentication header as a Bearer token.
 *
 * The AwsCognitoJwtAuthenticationFilter will look for the ID-Token in the header, verify it, and then create
 * Spring Security Role(s) based on the cognito groups passed in the ID-Token
 *
 * @author Cliff
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecConfig extends WebSecurityConfigurerAdapter implements Ordered {

    private int order = 4;

    @Autowired
    private AwsCognitoJwtAuthenticationFilter awsCognitoJwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //this will allow swagger UI and h2-console through spring-security
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources",
                "/swagger-resources/configuration/security", "/swagger-ui.html", "/webjars/**", "/console/*",
                "/actuator/**","/assets/**","/favicon.ico");
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.headers().cacheControl();
        httpSecurity
                .cors()
                    .and()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(
                            "/api/v1/auth/**",
                            "/cognito-signin*",
                            "/cognito-signout*")
                        .permitAll()
                    .antMatchers("/api/v1/**")
                        .authenticated() //all other API paths need to be authenticated
                    .antMatchers(
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                            "/")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
                .addFilterBefore(awsCognitoJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.headers().frameOptions().disable();
    }

    @Override
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
}
