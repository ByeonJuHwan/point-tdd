package io.hhplus.tdd.Integration

import io.hhplus.tdd.application.PointService
import io.hhplus.tdd.database.UserPointTable
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@SpringBootTest
class PointIntegrationTest {

    @Autowired
    private lateinit var userPointTable: UserPointTable

    @Autowired
    private lateinit var pointService: PointService

    private val logger: Logger = LoggerFactory.getLogger(PointIntegrationTest::class.java)


    /**
     *  Transactional 로 묶여서 테스트 하는게 아니다보니 자동으로 rollback 이 되지 않아서
     *  테스트 진행 시 전에 했던 테스트 데이터를 사용해서 테스트가 망가지는 형상 때문에
     *  매 테스트 마다 전에 했던 테스트 데이터를 0으로 초기화 시켜야한다
     */
    @BeforeEach
    fun beforeEach() {
        val userId = 1L
        userPointTable.insertOrUpdate(userId, 0)
    }


    /**
     * 동시성 이슈 테스트
     * 저장요청 1개 , 사용요청 1개씩만 들어왔다는 가정
     */
    @Test
    fun `동시에 저장과 사용 요청이 1번씩 눌렀을때 Synchronized 로 동시성 이슈를 해결한다`() {
        val userId = 1L

        val threadCount = 2
        val executeService = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(threadCount)

        // 포인트 저장 쓰레드
        executeService.submit {
            try {
                pointService.chargeUserPoint(userId, 50)
                Thread.sleep(5000)
            } finally {
                latch.countDown()
            }
        }

        // 포인트 사용 쓰레드
        executeService.submit {
            try {
                pointService.useUserPoint(userId, 10)
            } catch (e: IllegalArgumentException) {
                logger.error(e.message)
            } finally {
                latch.countDown()
            }
        }

        latch.await()
        executeService.shutdown()

        val result = pointService.getUserPointById(userId)
        assertThat(result.point).isEqualTo(40)
    }

    /**
     *  동시성 이슈 테스트
     *  저장 2번 사용요청이 2번 왔다는 가정
     */
    @Test
    fun `동시에 저장과 사용 요청이 여러번 눌렀을때 Synchronized 로 동시성 이슈를 해결한다`() {
        val userId = 1L
        val threadCount = 4
        val executeService = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(threadCount)

        // 포인트 저장 쓰레드
        for (i in 0..1) {
            executeService.submit {
                try {
                    pointService.chargeUserPoint(userId, 50)
                    Thread.sleep(5000)
                } finally {
                    latch.countDown()
                }
            }
        }

        // 포인트 사용 쓰레드
        for (i in 0..1) {
            executeService.submit {
                try {
                    pointService.useUserPoint(userId, 10)
                } catch (e: IllegalArgumentException) {
                    logger.error(e.message)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executeService.shutdown()

        val result = pointService.getUserPointById(userId)
        assertThat(result.point).isEqualTo(80)

    }
}