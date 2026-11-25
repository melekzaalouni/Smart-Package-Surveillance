package com.SmartPackageSurveillance.service;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    public SecretKey getSigningKey() {
    	SecretKey key = Keys.hmacShaKeyFor(
				Decoders.BASE64.decode(secret));
		return key ; 
    }
	public String generateToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey())
				.compact();
	}
	public Claims extractAllClaims(String token  ) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
				}
	public String extractEmail(String token ) {
		return extractAllClaims(token).getSubject();
	}
	
	

}
