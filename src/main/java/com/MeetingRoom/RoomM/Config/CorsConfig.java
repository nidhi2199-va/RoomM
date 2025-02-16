//package com.MeetingRoom.RoomM.Config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // Allow CORS for frontend running on localhost:5174
//        registry.addMapping("/**") // Allow all endpoints
//                .allowedOrigins("http://localhost:5174","http://127.0.0.1:5174" ) // Allow the frontend URL
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
//                .allowedHeaders("*")// Allow all headers
//                .allowCredentials(true)
//                .maxAge(3600L); // Allow sending cookies (optional)
//    }
//}
