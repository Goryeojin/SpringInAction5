package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
    // 기존 방식
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/design", "/orders")
//                .access("hasRole('ROLE_USER')")
                .hasRole("USER")
                .antMatchers("/", "/**")
//                .access("permitAll")
                .permitAll()
                .and()
                .httpBasic();
    }
     */
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
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}password1")
                .and()
                .withUser("user2")
                .password("{noop}password2")
                .authorities("ROLE_USER");
    }
    // 공식 문서에서 권장하는 방식
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

    /* 2. Jdbc Authentication
    // 기존 방식
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource);
            .userByUsernameQuery(
                "select username, password, enabled from users " +
                "where username=?")
            .authoritiesByUsernameQuery(
                "select username, authority from authorities " +
                "where username=?")
            .passwordEncoder(new NoEncodingPasswordEncoder());
    }

    // Spring Security 5.7.0 M2
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                // 스프링 시큐리티 5 버전부터 의무적으로 PasswordEncoder 를 사용해 비밀번호를 암호화해줘야 한다.
                // 그러나 현재 users 테이블의 password 열에는 암호화되지 않은 데이터가 들어있기 때문에 암호화 코드를 추가하더라도 인증이 되지 않는다.
                // 비밀번호를 암호화하지 않는 인코더를 임시로 작성하고 사용할 것. 스프링 시큐리티의 SQL 쿼리를 내 SQL 쿼리로 대체하여 사용한다.
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//                .addScripts(JdbcDaoImpl.DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY) // 위 두 SQL 합친 것
                .build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails user = User.builder()
                .username( "user")
                .password("password")
                .roles("USER")
                .build();
        JdbcUserDetailsManager users =
                new JdbcUserDetailsManager(dataSource);
//        users.setUsersByUsernameQuery(JdbcDaoImpl.DEF_USERS_BY_USERNAME_QUERY);
//        users.setAuthoritiesByUsernameQuery(JdbcDaoImpl.DEF_AUTHORITIES_BY_USERNAME_QUERY);
        users.createUser(user);

        return users;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return new NoEncodingPasswordEncoder();
    }

     */

    /* 3. LDAP Authentication

     */
    // 기존 방식
    
}