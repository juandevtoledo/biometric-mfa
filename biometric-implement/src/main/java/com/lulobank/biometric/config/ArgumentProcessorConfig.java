package com.lulobank.biometric.config;

import com.lulobank.biometric.validation.ArgumentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArgumentProcessorConfig {

    @Bean
    public ArgumentProcessor getArgumentProcessor() {
        return new ArgumentProcessor();
    }
}
