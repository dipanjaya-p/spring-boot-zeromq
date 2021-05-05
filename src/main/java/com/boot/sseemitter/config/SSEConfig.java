package com.boot.sseemitter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class SSEConfig {

    /**Faker from github to create fake data to avoid database configuration mainly focuses on testing purpose */
  /*  @Bean
    public Faker faker() {
        return new Faker(new Locale("en_US"));
    }*/

   /* @Bean
    public WebMvcConfigurer corsConfigurer(){
        log.info("CONFIGURING CROSS ORIGIN");
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");

            }
        };
    }*/

    @Bean
    public Map<String, SseEmitter> emitterMap(){
        return new ConcurrentHashMap<>();
    }

    @Bean("fixedThreadPool")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(20);
    }

}
