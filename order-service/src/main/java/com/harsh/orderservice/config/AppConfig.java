package com.harsh.orderservice.config;

import com.harsh.matchingEngine.engine.MatchingEngineService;
import com.harsh.matchingEngine.engine.MatchingEngineServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public MatchingEngineService matchingEngineService() {
        return new MatchingEngineServiceImpl();
    }
}
