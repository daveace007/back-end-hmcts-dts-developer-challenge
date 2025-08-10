package com.richard.task.config;


import com.richard.task.Origin;
import com.richard.task.OriginRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class TaskCORSConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter(OriginRepository repo) {



        CorsConfigurationSource source = request -> {

            List<String> allowedOrigin = repo
                    .findAllOrigins()
                    .stream()
                    .map(Origin::uri)
                    .toList();

            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.setAllowedOrigins(allowedOrigin);
            return config;
        };
        return new CorsFilter(source);
    }

}
