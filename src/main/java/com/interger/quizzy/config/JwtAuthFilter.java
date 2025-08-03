package com.interger.quizzy.config;


import com.interger.quizzy.model.User;
import com.interger.quizzy.service.MyUserDetailsService;
import com.interger.quizzy.service.MyUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;

    // Constructor injection instead of field injection
    public JwtAuthFilter(JwtUtil jwtUtil, MyUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            final String userIdStr = jwtUtil.extractUserId(jwt);
            Long userId = Long.parseLong(userIdStr);

            if (userIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUserId(userId);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Create authentication token with user principal
                    UsernamePasswordAuthenticationToken authToken;

                    // If userDetails is our custom MyUserPrincipal, include the User object
                    if (userDetails instanceof MyUserPrincipal) {
                        User user = ((MyUserPrincipal) userDetails).getUser();
                        authToken = new UsernamePasswordAuthenticationToken(
                                user, // Store the full User object as the principal
                                null,
                                userDetails.getAuthorities()
                        );
                    } else {
                        authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                    }
                // added comment to check git user name
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log exception but don't stop the filter chain
            logger.error("Failed to validate JWT token", e);
        }

        filterChain.doFilter(request, response);
    }
}