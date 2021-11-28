package com.mds;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@EnableScheduling
@ComponentScan("com.mds.**")
public class SpringConfiguration
{
    @Value("${spring.data.mongodb.uri}")
    private String connString;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.mds"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(info());
    }

    private ApiInfo info() {
        return new ApiInfoBuilder().title("Mandate Scanner")
                .description("Mandate Scanner Description")
                .contact(new Contact("MathMan", "https://github.com/Math-Man",
                        "kayacan.goktug@gmail.com"))
                .version("0.1.0")
                //.license("Apache License Version 2.0")
                //.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    ///Mongo DB configuration for rest
    @Bean
    public MongoClient mongoClient()
    {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClient client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connString))
                .codecRegistry(codecRegistry)
                .build());

        return client;
    }


}
