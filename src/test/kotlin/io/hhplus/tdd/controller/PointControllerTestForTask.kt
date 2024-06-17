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
class PointControllerTestForTask @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {
    @MockBean
    private lateinit var pointService: PointService


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
}