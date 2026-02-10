# 1. 설계

Q1. 너는 나와 페어 프로그래밍을 하며 백엔드 분야의 프로젝트를 처리할거야.
진행할 프로젝트의 내용은 아래와 같아.

-------

매일 룰렛을 돌려 포인트를 획득하고, 획득한 포인트로 상품을 구매하는 서비스.

일일 예산 관리: 하루 총 100,000p 예산, 소진 시 당첨 불가
1일 1회 참여: 유저별 하루에 한 번만 룰렛 참여 가능
랜덤 포인트: 100p ~ 1,000p 범위에서 랜덤 지급
포인트 유효기간: 획득일로부터 30일 (만료된 포인트는 사용 불가)
상품 구매: 획득한 포인트로 상품 구매

기술 스택: Spring Boot 3.x + Kotlin, JPA, Swagger, MySQL

인증
- 로그인 (닉네임이나 아이디만 입력하는 간단한 방식)

어드민 기능
- 일일 예산 조회/설정
- 상품 CRUD (목록, 등록, 수정)
- 주문 취소 (포인트 환불)
- 룰렛 참여 취소 (포인트 회수)

사용자 기능
- 룰렛 참여 (1일 1회, 100~1000p 랜덤)
- 오늘 참여 여부 및 잔여 예산 확인
- 내 포인트 조회 (유효기간 포함) / 잔액 조회
- 상품 목록 조회
- 상품 주문 (포인트 차감)
- 주문 내역 조회

필수 기능
- 로그인 Mocking (닉네임만 입력)
- 룰렛 참여 (1일 1회, 100~1000p 랜덤)
- 일일 예산 초과 방지
- 포인트 유효기간 관리 (획득일 + 30일)
- 만료 예정 포인트 조회 (7일 이내 만료)
- 상품 CRUD (어드민)
- 상품 구매 (포인트 차감)
- API 문서화 (Swagger)

핵심로직
이 서비스의 핵심은 "하루 한정된 예산" 과 "1일 1회 참여" 라는 제약 조건을 수많은 유저가 동시에 몰리는 상황에서도 정확하게 지키는 것입니다.
1. 중복 참여 방지: 같은 유저가 동시에 두 번 요청할 때, 한 번만 성공해야 합니다.
2. 예산 소진 처리: 예산이 1,000p 남았는데 5명이 동시에 500p씩 당첨되려 한다면, 정확히
   예산 범위 내에서만 지급되어야 합니다.

----

위 내용을 기억해줘

Q2. 현재 사용자, 일일 룰렛, 룰렛 포인트 내용(사용자와 일일 룰렛 유니크 처리로 중복 체크 및 포인트 사용 처리), 물품, 주문의 도메인으로 나눴어. 도메인 분리 측면에서 적절해?

Q3. 룰렛 포인트 내용의 도메인 역할이 참여 처리, 만료 날짜 확인, 사용/만료/취소 처리 등 역할이 큰데 어떠한 상황에 나누면 좋을까?

Q4. 롤렛 포인트 내용의 도메인 이름 추천해줘

Q5. 포인트 만료 기간을 필드로 설정하는 것이 좋을까? 단순히 현재 날짜를 기준으로 30일 전 날짜와 비교해도 되기는 한데 정책이 바뀐다면? 아니면 사람마다 날짜가 바뀌는 기능이 생긴다면 확장성을 생각해서 필드를 추가하는게 좋겠지?

Q6. 사용자와 어드민의 처리 성격이 다르고, 서로가 간섭하면 안되는 구조여서 모듈로 분리한 후 따로 서버를 열거야. 멀티모듈로 처리할 예정이고, 응답 구조와 로그 추적과 같은건 common, 도메인은 domain 이렇게 할거야. Infra의 경우에는 외부 통신이나 캐시 설정이 명확히 없어서 없앨 예정이고. 추가적으로 고려할 설계가 있나?

Q7. 현재 구조는 domain 모듈에서 jpa로 처리할거라 infra에 디비 처리 안할거야

Q8. 주문 상태 확인을 위한 OrderStatus enum에는 결제 완료, 사용자에 의한 취소, 어드민에 의한 취소 넣을거야
USER_CANCELLED, ADMIN_CANCELLED 하고, 결제 완료는 뭐로하지


Q9. 내가 지금
1. 새로운 룰렛이 생기면 인메모리 캐시 저장해둔 잔여 금액 값을 삭제
2. 잔액 조회나, 룰렛 시도할때 인메모리 캐시 저장해둔 잔여 금액 조회할때 없다면 디비에서 조회하여 저장
3. 룰렛 성공 처리해서 디비에 모두 반영되면, 해당 값만큼 캐시에서 값도 감소
4. 만약, 어드민이 룰렛의 예산을 변경하면, 캐시 값을 삭제.
5. 캐시는 ttl 설정
이런 구조로 처리할 생각인데,
우선 흐름은 보지말고, 캐시 설정에 대해서는 결국에 infra 모듈이 있는게 책임이 명확하겠지?

Q10.
로그인 usecase 만드는 중인데, 패키지 명으로 service보다 usecase가 적절해?


Q14. 룰렛은 매일 자정과, 서버 최초 실행(없으면) 생성을 해야해 app모듈의 roulette에서 서버 최초 초기화 로직은 어디에 둬야하지

Q27. @Column(name = "remaining_budget", nullable = false)
var remainingBudget: Long = remainingBudget
protected set
남은 예산이 아닌, 사용 예산으로 바꿀건데 필드명 추천

Q28. 룰렛 참여하면 point_record라는 룰렛으로 얻은 포인트를 저장하는게 있거든
현재는 이 entity에 유저와 룰렛을 fk한단말야

하지만, 이 기록은 룰렛이 존재하는한 사용자가 삭제되어도 존재하는 데이터이고 (soft delete하더라도)
요청이 많은 처리에서 point_record로 인해 디비 조회를 2번 더 하며 데이터 무결성 지키면서 저장하는건 오버헤드라고 생각하거든

그래서, user_id와 roulette_id만 저장하고 처리하면 오버헤드도 줄거같은데 어떻게 생각해?

Q31. 포인트는 만료기한이 있어. 만료 기한은 변경 가능성이 있기에 상수로 둬야해. 이 상수는 어느 모듈에 두는게 좋을까? domain모듈?

Q47. 만료 알림을 위해 알림 도메인을 넣을거야

알림 종류 (7일전, 3일전), 생성일자, 만료일자, 만료 포인트, 사용자id
로 설계했는데 부족한게 있을까?

Q57. GetProductUseCase에서
상품 전체 조회 (페이징화)
특정 상품 조회 (id통해)
메서드명 추천해줘

Q59. 상품 조회 로직을 구현할거야
1. 상품 원자 업데이트 성공
2. 사용자 포인트(point_record) 사용 (만료일이 가까운 포인트부터 순차적으로 사용)

이런 순서를 생각하는데 놓치는 부분이 있을까

Q60. orders랑 point_record랑 n:m할거야
중간 테이블에는
얼마를 사용했는지 데이터를 넣을거고

테이블 명 추천좀 해줘

Q70.  @Query(
value = """
SELECT *
FROM orders
WHERE user_id = :userId
ORDER BY id DESC
""",
countQuery = """
SELECT COUNT(*)
FROM orders
WHERE user_id = :userId
""", nativeQuery = true
)

사용자가 주문한 내역 페이징 조회(soft delete 포함) 해당 쿼리 틀린 부분 있을까

Q73. 현재 사용자한테 포인트 부채가 있어. 포인트 부채는 아래 같은 상황에서 발생해
1. 포인트를 획득하여 상품 구매
2. 어드민이 포인트 회수
3. 해당 포인트는 부채로 들어가 다음 룰렛 포인트로 삭감

이런 상황에서 만약 위 상황의 1번 포인트로 구매한 상품을 환불하면
사용자의 부채 감소 → 하지만, 이미 3번에서 룰렛 포인트로 삭감함

위와 같은 문제가 발생하니 처리하려는 방식은
부채를 갚은 룰렛 포인트를 기록하는거야

해당 방식의 문제가 될만한 곳좀 알려줘

Q74. 부채를 처리한 룰렛 포인트는 기록을 해둘거고, 부채를 갚은 포인트 또한 회수가 될 수 있어.
그렇기 떄문에
회수된 포인트를 사용한 포인트 환불 처리할떄
가장 오래된 부채 상황 포인트부터 조회하며 순차적으로 처리할거야
부채 상황 포인트 또한 회수된거라면 그때 데이터를 상태를 변경해서 다음 부터는 부채 삭감 데이터로 조회 안하면 돼

Q75. 취소하면 부채 이력도 확인하고 하는거 설명 적기


Q77. 룰렛 금액 변경은 날마다 다르게 적용할 수 있게 할거야
예를 들어 최대 일주일까지 어드민이 설정할 수 있고, 그 값을 저장해두고 룰렛을 생성할때 저장한 값을 들고와 적용할거야
만약, 없다면 디폴트 10만원 그대로 둘거고 해당 테이블명좀 추천해줘

Q79. 금일 예산은 증가만 사용 가능하다는 예외 문구 enum 값과 현재 날짜가 아닌 이후 날짜만 설정 가능하다는 enum 값 명좀 추천해줘

Q80. 어드민이 금일 예산 수정하면, 캐시되어 있던 총 예산을 무효화 해야해
지금 서버 인스턴스가 다르고, redis와 같은 애플리케이션을 안쓰고 그냥 서버 내에서의 캐시를 쓰니깐 공유가 안돼
현재 구조에서 가장 최적의 방법은 예산 수정하면, 서버간 통신을 해야하는데 처리 구조좀

Q82. 어드민 서버는 인증을 해야해 모든 api에. 스프링 시큐리티는 안써
필터 vs 인터셉트 vs aop 뭐가 좋을려나?
모든 api요청에 넣어야하고, X-User-Id가 오면 db에서 해당 값으로 조회하고, Role가 ADMIN인지 확인


# 2. 문제 고민과 해결

Q16.
@Transactional
fun createTodayIfNotExists() {
val today = LocalDate.now()

        try {
            dailyRouletteService.save(
                DailyRoulette(
                    rouletteDate = today
                )
            )
            log.info("[Roulette] 금일 룰렛 생성 완료 - date={}", today)
        } catch (e: DataIntegrityViolationException) {
             log.info("[Roulette] 이미 존재하여 생성 스킵 - date={}", today)
        }
    }

아무리 date에 유니크 속성이 있다고 하더라도 이미 존재하는지 한번 더 확인하는게 좋을까? 아니면 디비의 unique에 의존하는게 좋을까
하루에 한번 실행하는 로직이야

Q21.
override fun put(cacheName: String, key: Any, value: Any) {
val cache = requireCache(cacheName)
cache.put(key, value)
}


cacheRepository.put(
cacheName = CacheNames.REMAINING_BUDGET,
key = LocalDate.now().toString(),
value = 10000L
)

이런 형식으로 넣는데


org.springframework.dao.InvalidDataAccessApiUsageException: Cached value is not of required type [long]: 10000
at org.springframework.orm.jpa.EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(EntityManagerFactoryUtils.java:368) ~[spring-orm-6.2.15.jar:6.2.15]
at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:246) ~[spring-orm-6.2.15.jar:6.2.15]
at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.translateExceptionIfPossible(AbstractEntityManagerFactoryBean.java:560) ~[spring-orm-6.2.15.jar:6.2.15]
at org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61) ~[spring-tx-6.2.15.jar:6.2.15]

해당 오류 뭐야

Q25. 룰렛 참여 고려 사항은 아래와 같아
- 1일 1번 참여
- 동시 요청에 대한 데이터 정합성 확보
- 현재 금액을 기반으로 포인트 처리 상한선 확인
- 100~1000p 수령 가능

위 고려사항으로 내가 생각한 로직은 아래와 같아
0. ReentrantLock으로 락 및 공정성 처리
1. 캐싱된 잔여 포인트 조회 (만약 없으면 db에 접근하여 조회)
2. 잔여 포인트와 와 포인트 수령 상한선 중 작은 값으로 포인트 발급
3. 원자 업데이트 진행
4. 캐싱 데이터 업데이트 진행

이 로직에서 잘못된 부분에 대해 말해줘. 서버는 1대야

Q26. 1. lock으로 룰렛 처리 진행 순서를 관리하는거고, 잔여 금액도 lock이 걸려있기에 다른 사람이 업데이트를 못해
2. 캐시는 업데이트 성공하면 진행하는거야


Q65. Ci 과정에서 동시 요청 테스트가 오류 발생해
ParticipateRouletteConcurrencyTest > 100명이 동시에 participate 요청 시 정합성이 맞아야 한다() FAILED
org.springframework.dao.DataIntegrityViolationException at ParticipateRouletteConcurrencyTest.kt:57
Caused by: org.hibernate.exception.ConstraintViolationException at ParticipateRouletteConcurrencyTest.kt:57
Caused by: org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException at ParticipateRouletteConcurrencyTest.kt:57

이 오류이고, 로컬에서는 정상 동작해 어떤 이유인지 추측해줘

Q66. 로컬에서 돌려보니 테스트에서 DB를 공유해서 유니크 속성이 터지는거네
"user-$idx",
여기에 uuid 랜덤값 설정하게도 추가해줘

Q69.
어드민이 사용자 조회할 때, soft delete한 인원도 조회해야해
네이티브 쿼리로 작성해줘

전체 사용자 페이징 조회


# 3. 생산성 향상

Q11.
@Bean
public GroupedOpenApi OpenApi() {
String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Roulette-Up API v1")
                .pathsToMatch(paths)
                .build();
    }

해당 내용을 코틀린으로 바꿔줘

Q12.
@ApiResponses({
@ApiResponse(responseCode = "201", description = "회원가입 성공",
content = @Content(mediaType = "application/json", examples = {
@ExampleObject(name = "로그인 성공", value = """
{
"code": 200,
"message": "요청이 성공하였습니다."
}
""")
})),
@ApiResponse(responseCode = "400", description = "유효성 검증 실패",
content = @Content(mediaType = "application/json", examples = {
@ExampleObject(name = "유효성 검증 실패", description = "이메일의 경우 입력 여부 및 형식 검사", value = """
{
"code": "G002",
"errors": {
"password": "비밀번호는 필수 입력값입니다.",
"nickname": "닉네임은 필수 입력값입니다.",
"email": "잘못된 이메일 형식입니다.",
"username": "아이디는 필수 입력값입니다."
}
}
""")
})),

현재 내용과 관련없는 코드이고, 코틀린 스타일로만 바꿔줘


Q13.
@field:Size(min = 2, max = 30, message = "닉네임은 2~30자여야 합니다.")
val nickname: String,
해당 dto 필드에도 스웨거 스키마좀 넣어줘

Q15. 코틀린 로그 사용 설정 코드좀

Q17. 서버 최초 실행하면 해당 메서드를 호출하는 초기화 로직도 필요해 roulette 패키지에 들어가는거니 작성좀 해줘 적절한 클래스명과

Q18.
아래는 내가 테스트 코드를 작성하는 방법이야
@Nested
@DisplayName("로그인")
inner class SignIn {

        @Test
        fun `이미 존재하는 사용자는 회원가입을 진행하지 않는다` () {
            // given
            val request = mockk<SignInReq>(relaxed = true)
            val user = mockk<User>(relaxed = true)

            every { userService.readByNickname(request.nickname) } returns user

            // when
            val result = signInUseCase.signIn(request)

            // then
            assertEquals(user.id, result.id)
            verify(exactly = 1) { userService.readByNickname(request.nickname) }
            verify(exactly = 0) { userService.save(request.toEntity()) }
        }

        @Test
        fun `존재하지 않는 사용자는 회원가입을 진행한다`() {
            // given
            val request = mockk<SignInReq>(relaxed = true)
            val user = mockk<User>(relaxed = true)

            every { userService.readByNickname(request.nickname) } returns null
            every { userService.save(request.toEntity()) } returns user

            // when
            val result = signInUseCase.signIn(request)

            // then
            assertEquals(user.id, result.id)
            verify(exactly = 1) { userService.readByNickname(request.nickname) }
            verify(exactly = 1) { userService.save(request.toEntity()) }
        }
    }
}

이걸 참고해서
해당 클래스의 createTodayIfNotExists() 분기별로 테스트 코드 작성해줘

Q19. 카페인 캐시를 사용할거야
기본 설정좀 해줘

Q20. 어노테이션을 활용하는 것이 아닌, template형식으로 직접 다루고 싶어.
생성, 조회, 업데이트, 삭제 등이 존재해

Q22. getTodayRoulette()의 단위 테스트 코드를 작성해줘

Q23. getTodayRoulette() api 테스트 코드 작성해줘

Q24. mermaid로 시퀀스 다이어그램을 그릴거야
요청 → 캐시 조회 → 존재하면 반환
→ 존재하지 않으면 DB 조회 → DB에 존재하면 반환, 아니면 예외처리
틀좀 잡아줘

Q29. 1. 캐시된 예산 값 조회 (없으면 디비 조회)
2. 조회한 값과 1000 최소 값으로 100~1000 랜덤
3. 단일 연산 업데이트 (if == 0, 1 로 성공 확인)
4. point_record 저장 (user_id, roulette_id unique 속성 되어 있음)

5. 캐시 반영

로직 틀 잡아줘

Q30. daily_roulette엔티티의 roulette_date로 used_budget을 입력만큼 증가시키고, participant_count는 +1 시키는 쿼리 짜줘

Q32. user에는 available_point(사용 가능한 포인트), point_debt(포인트 부채)가 있어
point_debt가 0 이상이라면 발급 받은 포인트에서 차감하여 0으로 만들어야 하고, 포인트가 남는다면 available_point에 더해야해
쿼리 작성해줘

Q33. GREATEST(0, :point - point_debt) 같은 형식으로 사용 가능해?


Q34. /api/v1/roulettes/today/participations 에 k6로 동시 요청 테스트 진행

1. 1000명이 요청을 보내기
2. 헤더 X-User-Id에 userId 삽입. (중복 x)

K6 테스트 코드 짜줘

Q35. 그러면, 동시에 200명이 요청. 5번 나눠서 총 1000번 요청하는 형식으로 해줘

Q36. 해당 클래스의 participate() 컨트롤러 테스트 로직 작성해줘

Q37.
1. 락 획득
2. 총 예산 및 사용 예산 캐시 조회 (없으면 DB 조회)
3. 잔여 포인트가 100 포인트 미만이면 룰렛 예산 소진 처리
4. 포인트 랜덤 생성
5. 룰렛 정보 업데이트 및 참여 기록 처리 (여기서 트랜잭션을 시도하여 DB 커넥션 점유 최소화)
6. 캐시 갱신
7. 락 반납

mermaid 시퀀스 다이어그램 틀좀 만들어줘

Q38.
락 획득 시도 전에
if (pointRecordService.existsByUserIdAndRouletteDate(userId, today)) {
throw PointException(PointErrorType.ALREADY_PARTICIPATED)
}
확인해

Q38. 룰렛 참여 확인을 위한 유즈케이스 만들어야 하는데, 클래스명 추천

Q39. checkTodayParticipation메서드명 명확해?

Q40. 해당 클래스의 checkTodayParticipation 단위 테스트 작성해줘

Q41. 해당 클래스의 checkTodayParticipation 컨트롤러 테스트 작성해줘

Q42. 해당 클래스 getMyRecord() 단위 테스트 작성해줘

Q43. 변환 중 오류가 발생하니 실제 값을 만들어 넣고, id는 notnull로 보내니 리플랙션으로 넣어줘

Q44. 해당 클래스의 getMyRecords() 컨트롤러 테스트 코드 작성해줘

Q45. given(getPointRecordUseCase.getMyRecords(eq(userId), any(Pageable::class.java)))
.willReturn(PageImpl(emptyList(), pageable, 0))
에서
org.mockito.exceptions.misusing.InvalidUseOfMatchersException:
Misplaced or misused argument matcher detected here:

에러가 발생해 어떤 문제가 있는거야

Q46. 찾아보니 모키토가 매쳐 사용하니 null을 반환해. 코틀린 모키토는 인스턴스로 반환한다는데 디펜던시 좀 줘봐

Q46. @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
해당 파라미터 스웨거 설명 추가

Q48. 해당 내용을 코틀린으로 바꿔줘

Q49. 오전 11시마다 스케줄링 할거야

스케줄링을 하면, 포인트 만료 알림을 줄거고 point_record의 expires_at을 기준으로 하여 7일 전 1번, 3일 전 1번 조회 해야해
코드 구조 틀 잡아줘

Q50. 포인트 만료 처리 스케줄러 등록할거야
1. 자정에 시작
2. 해당 날짜와 동일한 expires_at을 가진 point_record의 status를 EXPIRED으로 업데이트
   코드 구조 짜줘


Q51. 해당 클래스의 generateExpiryNoticesForToday() 단위 테스트 코드 작성해줘

Q52. 해당 클래스 expireTodayPoints 단위 테스트 작성해줘

Q53. 사용자의 알림을 조회하는 로직
1. Notification을 userId로 조회 (커서 기반 진행)
2. 조회 후 데이터와 다음 페이지 있는지에 대한 정보 반환 (요청 개수보다 1 많이 조회하여 처리)
   코드 구조 짜줘


Q54. 알림 읽음 처리해야해. 내용이 같아서 조회는 안해줘도 돼. 유즈케이스 이름좀 추천해줘

Q55. 해당 클래스의 getNotificationsSliceByCursor 단위테스트 코드 짜줘

Q56. 사용자 엔티티의 point_debt 부채를 감소시키는 편의 메서드가 필요해

Q58. 해당 클래스 단위 테스트 코드 작성해줘

Q61. 다시 순서를 말해줄게

1. 잔액 조회를 통해 가능 여부 확인 (userId로 전체 조회. 많아도 30개)
2. 가능하다면 재고 원자 업데이트
3. order 생성
4. 재고 업데이트 성공하면, 1번에서 조회한 데이터를 활용해 만료가 가까운거부터 처리
5. 만료가 가까운 포인트부터 처리된 것은 order_point_usage에 삽입

구조 틀 짜줘

Q62. 내가 원하는건
1. PointRecord는 더티체킹을 이용한다. (remainingPoint이 되면 status는 USED로 만들어야해)
2. 처리되는 것들마다 OrderPointUsage를 만들어 list에 넣는다.
3. 호출자에 리스트 반환


Q63. 해당 로직 purchaseProduct 단위 테스트 진행해

Q64. 해당 클래스 동시 요청에 대해 테스트 할거야
상품의 quantity를 100, price 1
사용자마다 포인트 1000
만들어서 100명이 동시에 요청하는 테스트 코드좀 짜줘


Q67. 주문 취소 로직은
1. Order 조회하여 status를 USER_CANCELLED로 변경
2. OrderPointUsage 리스트를 order_id로 조회
3. 해당 리스트의 point_id로 point_record 내역 조회해서 OrderPointUsage의 usedAmount만큼 remaining_point 업데이트 (status가 USED면 해당 usedAmound만큼 증가 후 AVAILABLE 상태로 변경)
4. order의 product_id를 통해 해당 product를 order의productQuantity 만큼 증가

구조 틀 잡아줘


Q68.
이제 어드민 기능을 시작할거야 어드민 모듈에서
사용자 조회 유즈케이스 이름 추천해줘

Q71. class AdminProductReq 안에
상품 생성 data class
상품 업데이트 data class
재고 업데이트 data class
생성해줘

Q72. 해당 DTO 스웨거 스키마 작성해줘

Q76. 어드민 포인트 회수 로직 작성해야해
유즈케이스 이름 추천해주고

로직 과정은
1. pointRecordId로 조회하여 회수 처리
2. 만약, 일부라도 사용했다면 남은 금액은 해당 사용자의 pointDebt에 더하기
3. 해당 날짜(PointRecord의 rouletteDate)의 룰렛 사용 포인트 증가

로직 틀 짜줘

Q78. 어드민 룰렛 예산은 두개로 처리할거야
1. 금일 예산 수정
- 증가하는 값으로만 변경 가능
- dailyRoulette의 값을 수정
2. 이후 날짜의 예산 수정
- 증가 감소하는 값 모두 가능
- roulettebudgetsetting에서 해당 날짜 값을 조회 하여 수정
- 만약 값이 없다면, save 시도

틀좀 짜줘

Q81. 컨트롤러 테스트에 필터를 안타게 설정하는 어노테이션 설정좀 해줘

Q83. 인터셉터로 구현하려고 하는데, 틀좀 짜줘 ExceptionHandler까지 사용해서 응답 에러 구조화 할거야

Q84. 어드민 확인 필터에서 Preflight때문에 cors 오류가 발생해 필터에서 OPTIONS 요청은 허용해줘

Q85. @Query(
value = """
SELECT *
FROM point_record
WHERE roulette_date = :date
ORDER BY id DESC
""",
countQuery = """
SELECT COUNT(*)
FROM point_record
WHERE roulette_date = :date
""", nativeQuery = true
)
fun findAllByRouletteDateIncludeDeleted(
@Param("date") date: LocalDate,
pageable: Pageable
): Page<PointRecord>

닉네임도 필요해. 그래서 쿼리dsl로 바꿀거야. 이미 설정은 다 했어 쿼리dsl로 바꿔봐


Q86. 사용자 포인트 총합을 가져와야해. Point_record의 remaining_point의 총합이고, status는 AVAILABLE인 포인트야. 그리고, user의 point_debt를 뺴서 보여주면 돼.

