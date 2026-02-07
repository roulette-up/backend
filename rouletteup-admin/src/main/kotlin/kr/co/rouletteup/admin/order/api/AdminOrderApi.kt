package kr.co.rouletteup.admin.order.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "[주문 내역 API (ADMIN)]", description = "관리자 주문 내역 처리 관련 API")
interface AdminOrderApi {

    @Operation(summary = "사용자별 주문 내역 페이징 조회", description = "사용자별 주문 내역 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "사용자별 주문 내역 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "사용자별 주문 내역 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 3,
                                        "quantity": 4,
                                        "productPrice": 100,
                                        "productName": "상품3",
                                        "status": "USER_CANCELLED",
                                        "userId": 1,
                                        "productId": 9,
                                        "createdAt": "2026-02-07T14:35:00.489485",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 2,
                                        "quantity": 4,
                                        "productPrice": 100,
                                        "productName": "상품2",
                                        "status": "COMPLETED",
                                        "userId": 1,
                                        "productId": 6,
                                        "createdAt": "2026-02-07T10:56:26.427338",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 1,
                                        "quantity": 2,
                                        "productPrice": 100,
                                        "productName": "상품1",
                                        "status": "COMPLETED",
                                        "userId": 1,
                                        "productId": 1,
                                        "createdAt": "2026-02-07T10:55:49.72708",
                                        "deletedAt": null
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 3,
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
    fun getOrdersByUserId(
        @PathVariable userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "상품별 주문 내역 페이징 조회", description = "상품별 주문 내역 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품별 주문 내역 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품별 주문 내역 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 6,
                                        "quantity": 4,
                                        "productPrice": 100,
                                        "productName": "상품6",
                                        "status": "USER_CANCELLED",
                                        "userId": 1,
                                        "productId": 1,
                                        "createdAt": "2026-02-07T14:35:00.489485",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 4,
                                        "quantity": 4,
                                        "productPrice": 100,
                                        "productName": "상품4",
                                        "status": "COMPLETED",
                                        "userId": 1,
                                        "productId": 1,
                                        "createdAt": "2026-02-07T10:56:26.427338",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 1,
                                        "quantity": 2,
                                        "productPrice": 100,
                                        "productName": "상품1",
                                        "status": "COMPLETED",
                                        "userId": 1,
                                        "productId": 1,
                                        "createdAt": "2026-02-07T10:55:49.72708",
                                        "deletedAt": null
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 3,
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
    fun getOrdersByProductId(
        @PathVariable productId: Long,

        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "특정 주문 내역 조회", description = "특정 주문 내역 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "특정 주문 내역 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "특정 주문 내역 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 6,
                                "quantity": 4,
                                "productPrice": 100,
                                "productName": "상품6",
                                "status": "USER_CANCELLED",
                                "userId": 1,
                                "productId": 1,
                                "deletedAt": null
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
}