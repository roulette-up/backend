package kr.co.rouletteup.internal.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "internal.admin")
data class AdminInternalAuthProperties(
    val token: String,
    val tokenHeaderName: String,
)
