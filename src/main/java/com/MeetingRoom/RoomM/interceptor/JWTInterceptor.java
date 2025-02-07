package com.MeetingRoom.RoomM.interceptor;

import com.MeetingRoom.RoomM.Utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip authentication for signup and login routes
        String uri = request.getRequestURI();
        if (uri.contains("/user/signup") || uri.contains("/user/login")) {
            return true; // Allow public routes (signup, login)
        }

        // Extract the JWT token from Authorization header
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - Missing or Invalid Token");
            return false;
        }

        try {
            // Remove the "Bearer " prefix
            token = token.substring(7);

            // Validate the token
            String email = jwtUtil.extractEmail(token);

            // If the token is valid, the email will be extracted
            if (email == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized - Invalid Token");
                return false;
            }
            return true; // Valid token, continue with the request
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            // If token is expired or malformed
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Catch any other errors and handle them
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - " + e.getMessage());
            return false;
        }
    }
}
