package com.gregory.helpdesk.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	@Value("${jwt.secret}")
	private String secret;
	
	//metodo que vai gerar o token
	public String generateToken(String email) {
		
		return Jwts.builder()
				.setSubject(email)//gera o assunto com o email
				.setExpiration(new Date(System.currentTimeMillis() + expiration))//usa a variavel de expiração
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())//usa o algoritimo hs512 para embaralhar a palavra secreta
				.compact();//compacta o corpo do jwt
	}

	public boolean tokenValido(String token) {
		
		Claims claims = getClaims(token);
		if (claims != null) {
			String userName = claims.getSubject();
			Date expirationDate = claims.getExpiration();
			Date now = new Date(System.currentTimeMillis());
			if (userName != null && expirationDate != null && now.before(expirationDate)) {
				return true;
			}
		}
		return false;
	}

	public String getUserName(String token) {
		Claims claims = getClaims(token);
		
		if (claims != null) {
			return claims.getSubject();
		}
		return null;
		
	}
	
	private Claims getClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
			} catch (Exception e) {
			return null;
		}
	}
}
