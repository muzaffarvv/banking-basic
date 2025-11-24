package uz.pdp.exercises.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Banking System")
                    .description("In-Memory Banking System with REST API")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Banking System")
                            .email("support@banking.uz")
                    )
            )
    }
}