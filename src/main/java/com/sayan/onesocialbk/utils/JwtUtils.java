package com.sayan.onesocialbk.utils;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.sayan.onesocialbk.models.Person;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {
    private static final String JWT_SECRET = "secret";

    public static String generateJwtToken(Person user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TimeUnit.HOURS.toMillis(1));

        return Jwts.builder()
            .setSubject(user.getId())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
            .compact();
    }

    public static String parseUserIdFromJwtToken(String jwtToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims.getSubject();
    }

    public static Person parseJwtToken(String jwtToken) {
        Claims claims = Jwts.parser()
            .setSigningKey(JWT_SECRET)
            .parseClaimsJws(jwtToken)
            .getBody();

        String userId = claims.getSubject();
        Date expiration = claims.getExpiration();

        Person user = new Person();
        user.setId(userId);
        user.setExpiration(expiration);

        return user;
    }
}
