package com.campus.market.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * JwtTokenService 业务组件。
 *
 * @author 阿德
 * @date 2026/05/14
 */
@Service
public class JwtTokenService {

    private final JwtProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.secret());
        this.verifier = JWT.require(algorithm)
                .withIssuer(properties.issuer())
                .build();
    }

    public String createToken(Long userId, String username, List<String> roles) {
        var now = Instant.now();
        return JWT.create()
                .withIssuer(properties.issuer())
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(properties.expirationMinutes(), ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
