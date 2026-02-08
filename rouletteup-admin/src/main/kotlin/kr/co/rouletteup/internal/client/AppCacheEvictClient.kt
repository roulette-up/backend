package kr.co.rouletteup.internal.client

import java.time.LocalDate
import kr.co.rouletteup.internal.properties.AppInternalClientProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AppCacheEvictClient(
    private val restClient: RestClient,
    private val props: AppInternalClientProperties,
) {

    fun evictTotalBudget(date: LocalDate) {
        restClient.delete()
            .uri("${props.baseUrl}/internal/v1/admin/cache/total-budget/{date}", date.toString())
            .header(props.tokenHeaderName, props.token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toBodilessEntity()
    }
}
