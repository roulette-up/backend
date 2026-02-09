package kr.co.rouletteup.admin.product.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "[상품 API (ADMIN)]", description = "관리자 상품 처리 관련 API")
interface AdminProductApi {

    @Operation(summary = "상품 생성", description = "상품 생성 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 생성 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 생성 성공",
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
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "정보들 공백으로 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "name": "상품명은 필수입니다.",
                                "price": "가격은 필수입니다.",
                                "stockQuantity": "재고는 필수입니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "정보들 제약 조건을 만족하지 않는 경우",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "name": "상품명은 2~100자여야 합니다.",
                                "price": "가격은 1 이상이어야 합니다.",
                                "stockQuantity": "재고는 1 이상이어야 합니다."
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun createProduct(
        @RequestBody @Valid request: AdminProductReq.Create,
    ): ResponseEntity<*>


    @Operation(summary = "전체 상품 페이징 조회", description = "전체 상품 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "전체 상품 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "전체 상품 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 3,
                                        "name": "상품3",
                                        "stockQuantity": 23,
                                        "price": 3000
                                    },
                                    {
                                        "id": 2,
                                        "name": "상품2",
                                        "stockQuantity": 56,
                                        "price": 2000
                                    },
                                    {
                                        "id": 1,
                                        "name": "상품1",
                                        "stockQuantity": 10,
                                        "price": 1000
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
        ),
        ApiResponse(
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
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
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "특정 상품 조회", description = "특정 상품 조회 API")
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
                                "id": 6,
                                "name": "상품1",
                                "stockQuantity": 3,
                                "price": 133,
                                "createdAt": "2026-02-07T21:22:01.565634"
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
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

    @Operation(summary = "상품 정보 업데이트", description = "상품 정보 업데이트 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 정보 업데이트 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 정보 업데이트 성공",
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
            description = "유효성 검증 실패",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "정보들 공백으로 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "name": "상품명은 필수입니다.",
                                "price": "가격은 필수입니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "정보들 제약 조건을 만족하지 않는 경우",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "name": "상품명은 2~100자여야 합니다.",
                                "price": "가격은 1 이상이어야 합니다."
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun updateProductInfo(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.Update,
    ): ResponseEntity<*>

    @Operation(summary = "상품 재고 업데이트", description = "상품 재고 업데이트 API. 증감하고자 하는 크기를 입력 (원하는 재고 결과를 입력하는게 아님)")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 재고 업데이트 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 재고 업데이트 성공",
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
            description = "유효성 검증 실패",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "정보들 공백으로 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "increaseStock": "업데이트 하고자 하는 수량은 필수입니다."
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun updateProductStock(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.UpdateStock,
    ): ResponseEntity<*>

    @Operation(summary = "상품 삭제", description = "상품 삭제 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "상품 삭제 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 삭제 성공",
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
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun deleteProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<*>
}