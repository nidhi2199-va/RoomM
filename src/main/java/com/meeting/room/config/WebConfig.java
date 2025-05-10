package com.meeting.room.config;

import com.meeting.room.interceptor.JWTInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private JWTInterceptor jwtInterceptor;
    @Autowired
    public WebConfig(JWTInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register JWT interceptor and exclude login and signup endpoints
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("/user/signup", "/user/login"); // Exclude signup and login
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Apply to all endpoints
                .allowedOrigins("http://localhost:5173")  // Replace with your frontend's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allowed HTTP methods
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")  // Allowed headers
                .allowCredentials(true)  // Allow cookies and authorization headers
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
