package kr.co.rouletteup.infrastructure.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Roulette-Up",
        description = "Roulette-Up API 명세서.",
        version = "v1"
    )
)
class SwaggerConfig {

    @Bean
    fun openApi(): GroupedOpenApi {
        val paths = arrayOf("/**")

        return GroupedOpenApi.builder()
            .group("Roulette-Up API v1")
            .pathsToMatch(*paths)
            .build()
    }
}
