package com.central.api_gateway.util;

import com.central.api_gateway.exception.InvalidJWTTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT (JSON Web Token) operations including token generation and validation.
 * This class handles all JWT-related operations using the jjwt library.
 */
@Component
public class JwtUtil {

    /**
     * The secret key used for signing and verifying JWT tokens.
     * Injected from application properties and base64 decoded.
     */
    private final Key secretKey;

    /**
     * Constructs a new JwtUtil with the provided secret key.
     * The secret is expected to be a base64-encoded string.
     *
     * @param secret The base64-encoded secret key for JWT signing/verification
     */
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Decode the base64 secret and create a secure key
        byte[] keyBytes = Base64.getDecoder()
                .decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validates the provided JWT token.
     * This method verifies the token's signature and checks if it has expired.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     * @throws InvalidJWTTokenException if the token is invalid or expired
     */
    public Boolean validateToken(String token) {
        try {
            // Parse and verify the token's signature
            Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            // The token's signature is invalid
            throw new InvalidJWTTokenException("Invalid JWT signature");
        } catch (JwtException e) {
            // Other JWT validation errors (expired, malformed, etc.)
            throw new InvalidJWTTokenException("Invalid or expired JWT token: " + e.getMessage());
        }
    }
    /**
     * Extracts the user code from the JWT token.
     * The user code is expected to be stored in the "userCode" claim of the token.
     *
     * @param token The JWT token from which to extract the user code
     * @return The user code as a String
     * @throws InvalidJWTTokenException if the token is invalid or doesn't contain a user code
     */
    public String extractUserCode(String token) {
        try {
            // Parse and verify the token's signature, then extract the user code
            return Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userCode", String.class);
        } catch (SignatureException e) {
            throw new InvalidJWTTokenException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new InvalidJWTTokenException("Invalid or expired JWT token: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidJWTTokenException("Error extracting user code from token: " + e.getMessage());
        }
    }

    /**
     * Extracts the user role from the JWT token.
     * The user role is expected to be stored in the "role" claim of the token.
     *
     * @param token The JWT token from which to extract the user role
     * @return The user role as a String
     * @throws InvalidJWTTokenException if the token is invalid or doesn't contain a role
     */
    public String extractUserRole(String token) {
        try {
            // Parse and verify the token's signature, then extract the user role
            return Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
        } catch (SignatureException e) {
            throw new InvalidJWTTokenException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new InvalidJWTTokenException("Invalid or expired JWT token: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidJWTTokenException("Error extracting user role from token: " + e.getMessage());
        }
    }
}