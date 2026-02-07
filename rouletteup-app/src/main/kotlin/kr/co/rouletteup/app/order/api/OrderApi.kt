package kr.co.rouletteup.app.order.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.rouletteup.app.order.dto.OrderReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "[상품 주문 API]", description = "상품 주문 관련 API")
interface OrderApi {

    @Operation(summary = "상품 구매하기", description = "상품 구매하기 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 구매 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 구매 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다."
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "실패 - 불충분한 가격 및 재고",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "포인트 부족",
                            value = """
                            {
                              "code": "OR001",
                              "message": "포인트가 부족합니다."
                            }
                            """
                        ),
                        ExampleObject(
                            name = "상품 재고 부족",
                            value = """
                            {
                              "code": "OR002",
                              "message": "상품 재고가 부족합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "실패 - 상품 및 사용자 존재하지 않음",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 존재하지 않음",
                            value = """
                            {
                              "code": "PR001",
                              "message": "상품이 존재하지 않습니다."
                            }
                            """
                        ),
                        ExampleObject(
                            name = "사용자 존재하지 않음",
                            value = """
                            {
                              "code": "U001",
                              "message": "사용자가 존재하지 않습니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun purchaseProduct(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @RequestBody request: OrderReq,
    ): ResponseEntity<*>
}