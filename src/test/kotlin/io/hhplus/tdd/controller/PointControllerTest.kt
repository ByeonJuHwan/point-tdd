package io.hhplus.tdd.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.application.PointService
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(PointController::class)
class PointControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {
    @MockBean
    private lateinit var pointService: PointService

    // 유저 포인트 테스트
    @Nested
    @DisplayName("[조회] 유저 포인트 조회 컨트롤러 테스트")
    inner class GetUserPointControllerTests{
        /**
         * userId 를 요청 받으면 200 응답과 함께 UserPoint 정보를 내려준다.
         */
        @Test
        fun `getUserPointAPI Test 200 ok`() {
            val userId = 1L
            val userPoint = UserPoint(userId, 0,0)


            given(pointService.getUserPointById(userId)).willReturn(userPoint)

            mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(0))
                .andExpect(jsonPath("$.updateMillis").value(0))
        }

        /**
         * 유효하지 않은 Id가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `getUserPointAPI Test 400 Bad Request`() {
            val invalidUserId = "invalid"

            mockMvc.perform(get("/point/{id}", invalidUserId))
                .andExpect(status().isBadRequest)
        }
    }
    // 유저 포인트 테스트 끝


    @Nested
    @DisplayName("[조회] 유저 포인트 사용 내역을 조회 컨트롤러 테스트")
    inner class GetUserPointHistoriesControllerTests {
        /**
         * userId 를 받으면 해당 유저가 사용한 포인트 내역을 조회한다.
         */
        @Test
        fun `getUserPointHistoryAPI Test 200 Ok`() {
            val userId = 1L
            val histories = listOf(
                PointHistory(1, userId, TransactionType.CHARGE, 100, 0),
                PointHistory(2, userId, TransactionType.USE, 10, 0)
            )

            given(pointService.getUserPointHistories(userId)).willReturn(histories)

            mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].type").value(TransactionType.CHARGE.toString()))
                .andExpect(jsonPath("$[0].amount").value(100))
        }

        /**
         * 유효하지 않은 Id가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `getUserPointHistoryAPI Test 400 Bad Request`() {
            val invalidUserId = "invalid"

            mockMvc.perform(get("/point/{id}/histories", invalidUserId))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName ("[저장] 유저 포인트 저장 컨트롤러 테스트")
    inner class ChargeUserPointControllerTests {
        /**
         *  Id 와 amount (추가할 포인트양) 을 받으면 포인트를 저장한다.
         */
        @Test
        fun `chargeUserPointAPI Test 200 ok` () {
            val userId = 1L
            val amount = 100L
            val userPoint = UserPoint(userId, amount, System.currentTimeMillis())

            given(pointService.chargeUserPoint(userId, amount)).willReturn(userPoint)

            mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(amount))
                .andExpect(jsonPath("$.updateMillis").value(userPoint.updateMillis))
        }

        /**
         * 유효하지 않은 Id가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `chargeUserPointAPI Test 400 Bad Request Bad Id`() {
            val invalidUserId = "invalid"

            mockMvc.perform(patch("/point/{id}/charge", invalidUserId))
                .andExpect(status().isBadRequest)
        }

        /**
         * 유효하지 않은 amount 가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `chargeUserPointAPI Test 400 Bad Request Bad amount`() {
            val userId = 1L
            val invalidAmount = "invalidAmount"

            mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmount))
            ).andExpect(status().isBadRequest)
        }
    }


    @Nested
    @DisplayName ("[사용] 유저 포인트 사용 컨트롤러 테스트")
    inner class useUserPointControllerTests {
        /**
         *  userId 와 amount (사용할 포인트양) 을 받으면 포인트를 감소시킨다.
         */
        @Test
        fun `useUserPointAPI Test 200 ok`() {
            val userId = 1L
            val amount = 50L
            val userPoint = UserPoint(userId, amount, System.currentTimeMillis())

            given(pointService.useUserPoint(userId,amount)).willReturn(userPoint)

            mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(amount))
                .andExpect(jsonPath("$.updateMillis").value(userPoint.updateMillis))
        }

        /**
         * 유효하지 않은 Id가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `useUserPointAPI Test 400 Bad Request Bad Id`() {
            val invalidUserId = "invalid"

            mockMvc.perform(patch("/point/{id}/use", invalidUserId))
                .andExpect(status().isBadRequest)
        }

        /**
         * 유효하지 않은 amount 가 들어오면 400 에러를 반환한다.
         */
        @Test
        fun `useUserPointAPI Test 400 Bad Request Bad amount`() {
            val userId = 1L
            val invalidAmount = "invalidAmount"

            mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAmount))
            ).andExpect(status().isBadRequest)
        }
    }
}