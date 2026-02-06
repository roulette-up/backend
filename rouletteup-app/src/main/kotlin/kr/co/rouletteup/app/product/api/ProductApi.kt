package kr.co.rouletteup.app.product.api

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

@Tag(name = "[상품 API]", description = "상품 관련 API")
interface ProductApi {

    @Operation(summary = "상품 페이징 조회", description = "등록된 상품 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 3,
                                        "name": "상품3",
                                        "stockQuantity": 3,
                                        "price": 3000
                                    },
                                    {
                                        "id": 2,
                                        "name": "상품2",
                                        "stockQuantity": 2,
                                        "price": 2000
                                    },
                                    {
                                        "id": 1,
                                        "name": "상품1",
                                        "stockQuantity": 1,
                                        "price": 1000
                                    }
                                ],
                                "page": {
                                    "size": 3,
                                    "number": 0,
                                    "totalElements": 3,
                                    "totalPages": 0
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
    fun getProducts(
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

    @Operation(summary = "특정 상품 조회", description = "등록된 특정 상품 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "특정 상품 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "특정 상품 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 1,
                                "name": "상품1",
                                "stockQuantity": 1,
                                "price": 1000,
                                "createdAt": "2026-02-07T01:31:59.758401",
                                "modifiedAt": "2026-02-07T01:31:59.758401"
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
            description = "실패 - 상품 존재하지 않음",
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
                        )
                    ]
                )
            ]
        )
    )
    fun getProductById(
        @PathVariable productId: Long,
    ): ResponseEntity<*>
}