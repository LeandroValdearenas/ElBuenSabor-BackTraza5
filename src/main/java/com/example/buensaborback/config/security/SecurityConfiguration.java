package com.example.buensaborback.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;
    @Value("${auth0.audience}")
    private String audience;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;
    @Value("${CORS_ALLOWED_ORIGINS}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/wss/**").permitAll()
                                // INSUMOS
                                .requestMatchers(HttpMethod.GET, "/api/insumos/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/insumos/**").hasAnyAuthority("administrador", "cocinero")
                                .requestMatchers(HttpMethod.PUT, "/api/insumos/**").hasAnyAuthority("administrador", "cocinero")
                                // MANUFACTURADOS
                                .requestMatchers(HttpMethod.GET, "/api/manufacturados/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/manufacturados/**").hasAnyAuthority("administrador", "cocinero")
                                .requestMatchers(HttpMethod.PUT, "/api/manufacturados/**").hasAnyAuthority("administrador", "cocinero")
                                // CATEGORÍAS
                                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasAnyAuthority("administrador")
                                .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasAnyAuthority("administrador")
                                // CLIENTES
                                .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyAuthority("administrador", "cliente")
                                .requestMatchers(HttpMethod.POST, "/api/clientes/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasAnyAuthority("administrador", "cliente")
                                // EMPLEADOS
                                .requestMatchers(HttpMethod.GET, "/api/empleados/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/empleados/**").hasAnyAuthority("administrador")
                                .requestMatchers(HttpMethod.PUT, "/api/empleados/**").hasAnyAuthority("administrador", "cajero", "cocinero", "delivery")
                                // EMPRESAS
                                .requestMatchers(HttpMethod.GET, "/api/empresas/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/empresas/**").hasAnyAuthority("administrador") // superadmin?
                                .requestMatchers(HttpMethod.PUT, "/api/empresas/**").hasAnyAuthority("administrador") // superadmin?
                                // LOCALIDADES
                                .requestMatchers(HttpMethod.GET, "/api/localidades/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/localidades/**").hasAnyAuthority("administrador") // superadmin?
                                .requestMatchers(HttpMethod.PUT, "/api/localidades/**").hasAnyAuthority("administrador") // superadmin?
                                // MERCADOPAGO
                                .requestMatchers(HttpMethod.POST, "/api/mp/create_preference_mp").hasAnyAuthority("cliente")
                                // PAISES
                                .requestMatchers(HttpMethod.GET, "/api/pais/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/pais/**").hasAnyAuthority("administrador") // superadmin?
                                .requestMatchers(HttpMethod.PUT, "/api/pais/**").hasAnyAuthority("administrador") // superadmin?
                                // PEDIDOS
                                .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyAuthority("administrador", "cajero", "cliente", "cocinero", "delivery")
                                .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyAuthority("cliente")
                                .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyAuthority("administrador", "cajero", "cliente", "cocinero", "delivery")
                                // PROMOCIONES
                                .requestMatchers(HttpMethod.GET, "/api/promociones/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/promociones/**").hasAnyAuthority("administrador")
                                .requestMatchers(HttpMethod.PUT, "/api/promociones/**").hasAnyAuthority("administrador")
                                // PROVINCIAS
                                .requestMatchers(HttpMethod.GET, "/api/provincias/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/provincias/**").hasAnyAuthority("administrador") // superadmin?
                                .requestMatchers(HttpMethod.PUT, "/api/provincias/**").hasAnyAuthority("administrador") // superadmin?
                                // SUCURSALES
                                .requestMatchers(HttpMethod.GET, "/api/sucursales/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/sucursales/**").hasAnyAuthority("administrador") // superadmin?
                                .requestMatchers(HttpMethod.PUT, "/api/sucursales/**").hasAnyAuthority("administrador") // superadmin?
                                // UNIDADES DE MEDIDA
                                .requestMatchers(HttpMethod.GET, "/api/unidadesmedida/**").hasAnyAuthority("administrador", "cocinero")
                                .requestMatchers(HttpMethod.POST, "/api/sucursales/**").hasAnyAuthority("administrador")
                                .requestMatchers(HttpMethod.PUT, "/api/sucursales/**").hasAnyAuthority("administrador")
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(jwt ->
                                        jwt
                                                .decoder(jwtDecoder())
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                ).headers(headers -> headers.frameOptions(options -> options.sameOrigin()));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = Arrays.asList(corsAllowedOrigins.split(","));
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("X-Get-Header"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("http://elbuensabordashboard.api/roles");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
