package io.hhplus.tdd.domain

import io.hhplus.tdd.infra.PointRepository
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
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
    fun `특정 유저의 id를 받아서 infra layer 로 부터 현재 유저의 포인트를 받아온다`() { 
        val userId = 1L 
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis()) 
 
        `when`(pointRepository.findById(userId)).thenReturn(userPoint) 
        val result = point.getUserPointById(userId) 
 
        assertThat(result).isEqualTo(userPoint) 
    } 

    @Test
    fun `특정 유저의 id를 받아서 infra layer 로 부터 유저의 포인트 사용 내역을 받아온다`() {
        val userId = 1L
        val histories = listOf(
            PointHistory(1, userId, TransactionType.CHARGE, 100, 0),
            PointHistory(2, userId, TransactionType.USE, 10, 0)
        )

        `when`(pointRepository.getUserPointHistories(userId)).thenReturn(histories)
        val result = point.getUserPointHistories(userId)

        assertThat(histories).hasSize(2)
        assertThat(histories[0].amount).isEqualTo(100)
        assertThat(histories[1].amount).isEqualTo(10)
    }

    @Test
    fun `특정 유저의 저장할 포인트를 받아서 infra layer 로 전달하고 저장된 포인트를 받아온다`() {
        // given
        val userId = 1L
        val amount = 100L
        val totalPointsAfterCharge = 100L
        val chargedUserPoint = UserPoint(id = userId, point = totalPointsAfterCharge, updateMillis = System.currentTimeMillis())

        // when
        `when`(pointRepository.chargeUserPoint(userId = userId, amount = amount, totalPointsAfterCharge = totalPointsAfterCharge)).thenReturn(chargedUserPoint)
        val result = pointRepository.chargeUserPoint(userId, amount, totalPointsAfterCharge)

        // then
        assertThat(result).isEqualTo(chargedUserPoint)
    }

    @Test
    fun `특정 유저의 사용할 포인트를 받아서 infra layer 로 전달하고 사용되고 남아있는 포인트를 받아온다`() {
        val userId = 1L
        val amount = 100L
        val remainingPoints = 10L
        val remainingUserPoints = UserPoint(id = userId, point = remainingPoints, updateMillis = System.currentTimeMillis())

        `when`(pointRepository.useUserPoint(userId = userId, amount = amount, remainingPoints = remainingPoints)).thenReturn(remainingUserPoints)
        val result = pointRepository.useUserPoint(userId, amount, remainingPoints)

        assertThat(result).isEqualTo(remainingUserPoints)
    }
}
