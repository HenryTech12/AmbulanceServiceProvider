package org.flexisaf.intern_showcase.configurations;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Fakorode Odunayo Henry",
                        email = "henrytech874@gmail.com",
                        url = ""
                ),
                description = "Open Api Documentation for Ambulance Service Provider",
                title = "Flexisaf Api Specification",
                version = "1.0",
                license = @License(
                        name = "license"
                ),
                termsOfService = "Terms Of Service"
        )
        ,
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Enviroment"
                ),
                @Server(
                        url = "https://",
                        description = "Production Environment"
                )
        },
        security = @SecurityRequirement(
                name = "bearerAuth"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Bearer Authorization Token",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
