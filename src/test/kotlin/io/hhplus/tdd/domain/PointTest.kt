package io.hhplus.tdd.domain

import io.hhplus.tdd.infra.PointRepository
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointTest {

    @InjectMocks
    lateinit var point: Point

    @Mock
    lateinit var pointRepository: PointRepository

    @Test
    fun getUserPointById() {
    }

    @Test
    fun `특정 유저의 포인트를 저장한다`() {
        // given
        val userId = 1L
        val amount = 100L
        val totalPointsAfterCharge = 100L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        // when
        `when`(pointRepository.chargeUserPoint(userId = userId, amount = amount, totalPointsAfterCharge = totalPointsAfterCharge)).thenReturn(userPoint)
        val result = pointRepository.chargeUserPoint(userId, amount, totalPointsAfterCharge)

        // then
        Assertions.assertThat(result).isEqualTo(userPoint)
    }
}