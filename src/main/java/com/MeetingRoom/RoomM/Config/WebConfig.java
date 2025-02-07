package com.MeetingRoom.RoomM.Config;

import com.MeetingRoom.RoomM.interceptor.JWTInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JWTInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register JWT interceptor and exclude login and signup endpoints
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // Intercept all paths
                .excludePathPatterns("/user/signup", "/user/login"); // Exclude signup and login
    }
}
