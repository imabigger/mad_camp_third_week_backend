package com.momentum.momentum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MoMenTum API")
                        .version("1.0")
                        .description("MoMenTum app을 위한 API 명세서입니다.\n\n" +
                                "Back 담당자 : 이시준\n" +
                                "Email : mac520@naver.com\n\n" +
                                "Front 담당자 : 이효정\n" +
                                "Email : hyojung040102@naver.com")
                        .contact(new Contact()
                                .name("이시준 & 이효정")
                                .email("mac520@naver.com, hyojung040102@naver.com")
                        )
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }
}
