package guru.sfg.brewery.config;

import guru.sfg.brewery.security.JpaUserDetailsService;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    JpaUserDetailsService jpaUserDetailsService;

    public RestHeaderAuthFilter restHeaderAuthFilter(RequestMatcher matcher, AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(matcher);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestUrlAuthFilter restUrlAuthFilter(RequestMatcher matcher, AuthenticationManager authenticationManager) {
        RestUrlAuthFilter filter = new RestUrlAuthFilter(matcher);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        PathPatternRequestMatcher apiMatcher = PathPatternRequestMatcher.withDefaults().matcher("/api/**");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(restHeaderAuthFilter(apiMatcher, authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(restUrlAuthFilter(apiMatcher, authenticationManager), UsernamePasswordAuthenticationFilter.class);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/h2-console/**").permitAll() // do not use in production
                                .requestMatchers("/", "/webjars/**", "/resources/**").permitAll()
//                                .requestMatchers("/beers/find", "/beers/{beerId}").hasAnyRole("ADMIN","CUSTOMER", "USER")
//                                .requestMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").hasAnyRole("ADMIN","CUSTOMER", "USER")
//                                .requestMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.GET, "/brewery/breweries/**").hasAnyRole("ADMIN","CUSTOMER")
//                                .requestMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("ADMIN","CUSTOMER")
                                .anyRequest().authenticated()

                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // do not use in production, allows H2 console to work
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(this.userDetailsService());
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return jpaUserDetailsService;
//        return new InMemoryUserDetailsManager(
//                User.withUsername("spring")
//                        .password("{bcrypt}$2a$16$Ywy6irwTdCho5/oMIvm4AObJ5Pft3N5OOPNZuTqH1bYqtg3YC.GZq")
//                        .roles("ADMIN")
//                        .build(),
//                User.withUsername("user")
//                        .password("{sha256}bd0dcea39f034d83efcc3372780ddef53dcd04f3e8581164687da97c4e1d880fbc7bc96d6f26ccd9")
//                        .roles("USER")
//                        .build(),
//                User.withUsername("scott")
//                        .password("{ldap}{SSHA}FbF2v8SFEnERgxkuQLNukYOXykARbUUlcHxZCg==")
//                        .roles("USER")
//                        .build(),
//                User.withUsername("testApiKey")
//                        .password("{bcrypt}$2a$16$u8eNRyqqgd3rgR4z/FY3t.OavNKC5S1hBWecAEraKvL0Ua/CreKnK")
//                        .roles("API_KEY")
//                        .build()
//        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());

//        auth.inMemoryAuthentication()
//                .withUser("spring")
//                .password("{noop}guru")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{noop}password")
//                .roles("USER");
//    }
}