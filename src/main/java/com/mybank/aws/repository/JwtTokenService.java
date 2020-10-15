package com.mybank.aws.repository;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenService {
	private static final String AUTH = "auth";
	private static final String AUTHORIZATION = "Authorization";
	private String secretKey = "secret-key";
	private long validityInMilliseconds = 3600000; // 1h

	public JwtTokenService() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username, List<String> roles) {
		System.out.println(String.format("Generating token for {%s} with roles {%s} ", username, roles));
		Claims claims = Jwts.claims().setSubject(username);
		// claims.put ("auth",new SimpleGrantedAuthority ("ROLE_USER"));
		claims.put("auth", roles.stream().map(s -> "ROLE_" + s).collect(Collectors.toList()));
		// claims.put("auth", roles.stream().map(s -> new
		// SimpleGrantedAuthority("ROLE_"+s)).collect(Collectors.toList()));
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secretKey).compact();
	}

	public String resolveToken(String bearerToken) {
		// String bearerToken = req.getHeader(AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

	public boolean validateToken(String token) throws JwtException, IllegalArgumentException {
		Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		return true;
	}

	public List<String> getRoleList(String token) {
		return (List<String>) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(AUTH);
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean isTokenPresentInDB(String token) {
		return true;
	}

}
