package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
/**
 * WebSecurityConfigurerAdapter 클래스가 Deprecated 되어서 상속받아 Override 하는 방식이 아닌 Bean 등록 방식으로 변경.
 */
public class SecurityConfig {

    /** Spring Security 5.7.0-M2부터
     * HttpSecurity 설정이 기존에는 Override 하여 구현했으나 SecurityFilterChain 을 Bean 으로 등록하는 방식으로 바뀜.
     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/design", "/orders")
////                .access("hasRole('ROLE_USER')")
//                .hasRole("USER")
//                .antMatchers("/", "/**")
////                .access("permitAll")
//                .permitAll()
//                .and()
//                .httpBasic();
//    }
    // 공식 문서에서 권장하는 방식
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/design", "/orders")
                .hasRole("USER")
                .antMatchers("/", "/**")
                .permitAll()
                .and()
                .httpBasic();

        return http.build();
    }

    /* 1. In-Memory Authentication
    // 이전 방식
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                .password("{noop}password1")
//                .and()
//                .withUser("user2")
//                .password("{noop}password2")
//                .authorities("ROLE_USER");
//    }
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails user = User.withDefaultPasswordEncoder().username("user1").password("password1").roles("USER").build();
        UserDetails user2 = User.withDefaultPasswordEncoder().username("user2").password("password2").roles("USER").build();
//        UserDetails user2 = User.builder().username("user2").password("password2").roles("USER").build();
        return new InMemoryUserDetailsManager(user, user2);
    }

    // User.wthDefaultPasswordEncoder() 사용 불가로 아래처럼 생성하여 패스워드 인코딩.. 하는 방법이 있지만 개발 환경에서는 사용하도록 하자.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
     */
    
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource);
    }
}