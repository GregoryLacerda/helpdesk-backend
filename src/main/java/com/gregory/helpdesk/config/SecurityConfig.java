package com.gregory.helpdesk.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gregory.helpdesk.security.JWTAuthenticationFilter;
import com.gregory.helpdesk.security.JWTAuthorizationFilter;
import com.gregory.helpdesk.security.JWTUtil;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//anotação para poder colocar anotações nos endpoints
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private static final String[] PUBLIC_MATCHERS = {"/h2-console/**"};
	
	@Autowired
	private Environment env;//interface que representa o ambiente em que o aplicativo está sendo executado, como o perfil de test
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private UserDetailsService detailsService;
	
	//metodo que especifica os endpoints que precisam de segurança contra ataque
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//se estiver no perfil de teste, desabilita as opções que bloqueiam a visualização do h2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		//desabilita a proteção contra ataque de sessoes de usuarios
		http.cors().and().csrf().disable();
		
		//filtro criado para as autenticações
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
		
		//filtro criado para as autorizações
		http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, detailsService));
		
		//libera qualquer acesso ao h2 após a url do PUBLIC_MATCHERS
		http.authorizeRequests().antMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated();
		
		//desabilita a criação de sessões de usuarios
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(detailsService).passwordEncoder(bCryptPasswordEncoder());
	}
	
	// liberando as requisições no endpoint de forma explicita
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		//permite de forma padrão o acesso ao Get e Post e mais alguns
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		//libera o acesso aos metodos passados na lista
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPETIONS"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		//Registra a configração para receber requisição de todas as fontes
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	//cria um bean para que tenha esse password encoder em qualquer parte do codigo
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
