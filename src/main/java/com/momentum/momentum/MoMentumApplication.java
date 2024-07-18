package com.momentum.momentum;

import com.momentum.momentum.config.GameSettingsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoAuditing
@EnableConfigurationProperties(GameSettingsConfig.class)
public class MoMentumApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoMentumApplication.class, args);
    }
}
