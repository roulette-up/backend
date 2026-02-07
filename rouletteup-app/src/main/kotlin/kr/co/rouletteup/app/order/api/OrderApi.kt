package kr.co.rouletteup.app.order.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.rouletteup.app.order.dto.OrderReq
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
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

    @Operation(summary = "주문 내역 리스트 조회", description = "상품을 구매한 주문 내역 리스트 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "주문 내역 리스트 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "주문 내역 리스트 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 2,
                                        "quantity": 4,
                                        "productPrice": 100,
                                        "productName": "상품1",
                                        "status": "COMPLETED"
                                    },
                                    {
                                        "id": 1,
                                        "quantity": 2,
                                        "productPrice": 100,
                                        "productName": "상품2",
                                        "status": "USER_CANCELLED"
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 2,
                                    "totalPages": 1
                                }
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun getOrders(
        @RequestHeader(value = "X-User-Id") userId: Long,

        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)  
                - sort: 정렬 (기본 id, DESC)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "특정 주문 내역 조회", description = "상품을 구매한 특정 주문 내역 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "특정 주문 내역 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "특정 주문 내역 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 1,
                                "quantity": 2,
                                "productPrice": 100,
                                "productName": "상품1",
                                "status": "COMPLETED",
                                "productId": 1,
                                "createdAt": "2026-02-07T10:55:49.72708"
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "실패 - 주문 내역 존재하지 않음",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "주문 내역 존재하지 않음",
                            value = """
                            {
                                "code": "OR003",
                                "message": "주문이 존재하지 않습니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun getOrderById(
        @PathVariable orderId: Long,
    ): ResponseEntity<*>

    @Operation(summary = "주문 취소", description = "사용자가 본인의 주문을 취소하는 API")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주문 취소 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "주문 취소 성공",
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
                description = "실패 - 취소 불가한 주문 상태",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "취소 불가한 주문 상태",
                                value = """
                            {
                                "code": "OR005",
                                "message": "이미 취소된 주문입니다."
                            }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "403",
                description = "실패 - 본인 주문이 아님",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "본인 주문이 아님",
                                value = """
                            {
                                "code": "OR004",
                                "message": "주문 내역 처리 권한이 없습니다."
                            }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "실패 - 주문 내역 존재하지 않음",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "주문 내역 존재하지 않음",
                                value = """
                            {
                                "code": "OR003",
                                "message": "주문이 존재하지 않습니다."
                            }
                            """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun cancelOrder(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @PathVariable orderId: Long,
    ): ResponseEntity<*>
}