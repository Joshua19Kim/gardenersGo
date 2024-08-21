package nz.ac.canterbury.seng302.gardenersgrove.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Custom Security Configuration
 * Such functionality was previously handled by WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.baeldung.security"})
public class SecurityConfiguration {

    /**
     * Our Custom Authentication Provider {@link CustomAuthenticationProvider}
     */
    private final CustomAuthenticationProvider authProvider;

    /**
     * @param authProvider Our Custom Authentication Provider {@link CustomAuthenticationProvider} to be injected in
     */
    public SecurityConfiguration(CustomAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Create an Authentication Manager with our {@link CustomAuthenticationProvider}
     *
     * @param http http security configuration object from Spring
     * @return a new authentication manager
     * @throws Exception if the AuthenticationManager can not be built
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }


    /**
     * @param http http security configuration object from Spring (beaned in)
     * @return Custom SecurityFilterChain
     * @throws Exception if the SecurityFilterChain can not be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Allow h2 console through security. Note: Spring 6 broke the nicer way to do this (i.e. how the authorisation is handled below)
        // See https://github.com/spring-projects/spring-security/issues/12546
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/h2/**"),
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                AntPathRequestMatcher.antMatcher("/images/**"),
                                AntPathRequestMatcher.antMatcher("/javascript/**"),
                                AntPathRequestMatcher.antMatcher("/wordlists/**"),
                                AntPathRequestMatcher.antMatcher("/"),
                                AntPathRequestMatcher.antMatcher("/webjars/**"),
                                AntPathRequestMatcher.antMatcher("/login"),
                                AntPathRequestMatcher.antMatcher("/register"),
                                AntPathRequestMatcher.antMatcher("/forgotPassword"),
                                AntPathRequestMatcher.antMatcher("/identifyPlant"),
                                AntPathRequestMatcher.antMatcher("/resetPassword"),
                                AntPathRequestMatcher.antMatcher("/signup"))
                        .permitAll())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                // If you change this, you will need to ask for permission to add it to the VM as well
                                .policyDirectives("default-src 'self'; img-src 'self' data: https://cdn.weatherapi.com; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'")
                        )
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")))
                .authorizeHttpRequests(request -> request
                        // Allow "/", "/register", "/forgotPassword", "/resetPassword", "/signup" and "/login" to anyone (permitAll)
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/forgotPassword").permitAll()
                        .requestMatchers("/resetPassword").permitAll()
                        .requestMatchers("/signup").permitAll()

                        // Only allow admins to reach the "/admin" page
                        .requestMatchers("/admin").hasRole("ADMIN")
                        // Any other request requires authentication
                        .anyRequest()
                        .authenticated()
                )
                // Define logging in, a POST "/login" endpoint now exists under the hood, after login redirect to user page
                .formLogin(formLogin -> formLogin.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/home"))
                // Define logging out, a POST "/logout" endpoint now exists under the hood, redirect to "/login", invalidate session and remove cookie
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID"));
        return http.build();
    }
}
