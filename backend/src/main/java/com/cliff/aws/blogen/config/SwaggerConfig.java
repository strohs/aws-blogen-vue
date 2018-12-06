package com.cliff.aws.blogen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Config for OpenAPI (Swagger)
 * Using this to document the REST API
 *
 * @author Cliff
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("com.cliff.aws.blogen.api"))
                    .paths(PathSelectors.any())
                    .build()
                .pathMapping("/")
                .apiInfo(metaData())
                .securitySchemes( Arrays.asList(apiKey() ));

    }

    private ApiInfo metaData() {
        Contact contact = new Contact("Blogen Admin",
                "https://github.com/strohs/springboot-blogen-vuejs",
                "notreal@example.com");

        return new ApiInfo(
                "Blogen REST API",
                "OpenAPI (Swagger) docs for the Blogen REST API",
                "1.0",
                "Terms of Service",
                contact,
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }

    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header"); //`apiKey` is the name of the APIKey, `Authorization` is the key in the request header
    }

}
