package kr.co.rouletteup.internal.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "internal.app")
data class AppInternalClientProperties(
    val baseUrl: String,
    val token: String,
    val tokenHeaderName: String,
)
