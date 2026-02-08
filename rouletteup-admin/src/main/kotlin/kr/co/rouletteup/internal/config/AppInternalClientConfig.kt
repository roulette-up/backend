package kr.co.rouletteup.internal.config

import kr.co.rouletteup.internal.properties.AppInternalClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@EnableConfigurationProperties(AppInternalClientProperties::class)
class AppInternalClientConfig {

    @Bean
    fun restClient(): RestClient = RestClient.create()
}
