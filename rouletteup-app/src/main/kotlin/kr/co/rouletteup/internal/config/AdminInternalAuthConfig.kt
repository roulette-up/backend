package kr.co.rouletteup.internal.config

import kr.co.rouletteup.internal.properties.AdminInternalAuthProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AdminInternalAuthProperties::class)
class AdminInternalAuthConfig