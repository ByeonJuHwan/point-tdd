package io.hhplus.tdd.application

import io.hhplus.tdd.domain.Point
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @InjectMocks
    lateinit var pointService: PointService

    @Mock
    lateinit var point: Point


    /**
     * 사용자의 포인트 데이터를 domain layer 에서 받아온다.
     */
    @Test
    fun getUserPointById() {
        //given
        val userId = 1L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        // when
        `when`(point.getUserPointById(userId)).thenReturn(userPoint)
        val result = pointService.getUserPointById(userId)

        // then
        assertThat(result).isEqualTo(userPoint)
    }

    @Test
    fun `특졍유저의 아이디와 포인트를 받으면 domain 레이어에서 값을 받아온다`() {
        // given
        val userId = 1L
        val amount = 100L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        // when
        `when`(point.chargePoint(userId = userId, amount = amount)).thenReturn(userPoint)
        val result = pointService.chargeUserPoint(userId,amount)

        // then
        assertThat(result).isEqualTo(userPoint)
    }
    @Test
    fun useUserPoint() {
    }
}