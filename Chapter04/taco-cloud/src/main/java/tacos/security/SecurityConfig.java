package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.ldap.EmbeddedLdapServerContextSourceFactoryBean;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfigurerAdapter 클래스가 Deprecated 되어서 상속받아 Override 하는 방식이 아닌 Bean 등록 방식으로 변경.
 */
@Configuration
@EnableWebSecurity
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


    // 기존 방식
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .ldapAuthentication() // LDAP 기반 인증 스프링 시큐리티 구성을 위한 메서드
            .userSearchBase("ou=people") // 기본적으로 사용자와 그룹 모두 LDAP 기본 쿼리는 비어 있어서 LDAP 계층의 루트부터 수행됨
                // 사용자를 찾기 위한 기준점 쿼리 제공
            .userSearchFilter("(uid={0})") // LDAP 기본 쿼리의 필터 제공. 사용자 검색
            .groupSearchBase("ou=groups") // 그룹을 찾기 위한 기준점 쿼리 지정 => 루트부터 검색하는 것이 아니라
                // 사용자는 people 구성 단위(Organizational Unit, OU)부터, 그룹은 groups 구성 단위부터 검색 시작
            .groupSearchFilter("member={0}")
            .contextSource() // contextSource() 는 ContextSourceBuilder 를 반환하여 LDAP 서버의 위치를 지정할 수 있게 한다.
            .root("dc=tacocloud,dc=com") // 내장된 LDAP 서버 사용 시 원격 LDAP 서버 URL 설정(:url()) 대신 root() 메서드를 사용해 내장 서버의 루트 경로를 지정한다.
            .ldif("classpath:users.ldif") // LDAP 서버가 시작될 때 classpath 에서 찾을 수 있는 LDIF 파일로부터 데이터를 로드함.
                // 스프링이 classpath 를 검색하지 않고 LDIF 파일을 찾도록 한다면, ldif() 메서드를 사용해 LDIF 파일을 찾을 수 있는 경로를 지정한다.
            .and()
            .passwordCompare() // LDAP 기본 인증 전략 : 사용자가 LDAP 서버에서 인증.
                // 그러나 passwordCompare() 을 사용하여 입력된 비밀번호를 LDAP 디렉터리에 전송하고 이 비밀번호를 사용자의 비밀번호 속성 값과 비교하도록 LDAP 서버에 요청한다.
            .passwordEncoder(new BCryptPasswordEncoder()) // 비밀번호 암호화 인코더 지정. 서버 측에서 비밀번호 비교 시 실제 비밀번호가 서버에 유지된다.
                // bcrypt 암호화 해싱 인코더를 사용해 비밀번호 암호화, 이것은 LDAP 서버에서도 bcrypt 를 사용해 비밀번호가 암호화된다.
            .passwordAttribute("userPasscord"); // 로그인 폼에 입력된 비밀번호가 사용자의 LDAP 서버에 있는 userPassword 속성값과 비교됨.
            // 위 메서드로 비밀번호 속성의 이름을 지정한다. => 전달된 비밀번호와 userPasscode 속성 값과 비교할 것을 지정.

        // 이는 classpath 루트에서 users.ldif 파일을 찾아 LDAP 서버로 데이터를 로드하라고 요청한다.
    }
    */
    // version 5.7.0
    @Bean
    public EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean() {
        EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean = EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
        contextSourceFactoryBean.setRoot("dc=tacocloud,dc=com");
        contextSourceFactoryBean.setLdif("classpath:users.ldif");

        return contextSourceFactoryBean;
    }

    @Bean
    UnboundIdContainer ldapContainer() {
        return new UnboundIdContainer("dc=tacocloud,dc=com", "classpath:users.ldif");
    }
    @Bean
    AuthenticationManager ldapAuthenticationManager(BaseLdapPathContextSource contextSource) {
        LdapPasswordComparisonAuthenticationManagerFactory factory =
                new LdapPasswordComparisonAuthenticationManagerFactory(
                                                        contextSource, new BCryptPasswordEncoder());
        factory.setUserDnPatterns("uid={0},ou=people");
//        factory.setUserDetailsContextMapper(new PersonContextMapper());
        factory.setPasswordAttribute("userPasscode");
        return factory.createAuthenticationManager();
    }
}