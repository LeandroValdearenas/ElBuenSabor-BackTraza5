package com.example.buensaborback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("El Buen Sabor")
                        .version("1.0")
                        .description("<b>\"El Buen Sabor\"</b> Una aplicación de E-Commerce para empresas varias." +
                                "<h3> Integrantes: </h3>" +
                                "<ul> <li> Alejandro Lencinas </li> <li> Elias Santilli </li> <li> Leandro Valdearenas </li> </ul>")
                        .contact(new Contact()
                                .name("UTN - FRM"))
                        .contact(new Contact()
                                .name("Github")
                                .url("https://github.com/LeandroValdearenas/ElBuenSabor-BackTraza5")
                        )
                );
    }
}
