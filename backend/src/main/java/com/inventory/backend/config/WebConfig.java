package com.inventory.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String imagePath =
                "file:" + System.getProperty("user.home") + "/Stockify/images/";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagePath);
    }
}
