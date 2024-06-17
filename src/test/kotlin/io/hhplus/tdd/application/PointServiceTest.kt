package io.hhplus.tdd.application

import io.hhplus.tdd.domain.Point
import io.hhplus.tdd.exception.NotEnoughPointException
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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
    @Nested
    @DisplayName("[조회] 회원별 포인트 조회 서비스")
    inner class GetUserPointTests {

        @Test
        fun `사용자의 포인트 데이터를 domain layer 에서 받아온다`() {
            // given
            val userId = 1L
            val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

            // when
            `when`(point.getUserPointById(userId)).thenReturn(userPoint)
            val result = pointService.getUserPointById(userId)

            // then
            assertThat(result).isEqualTo(userPoint)
        }
    }

    @Nested
    @DisplayName("[저장] 회원별 포인트 저장 서비스")
    inner class ChargeUserPointTests {

        @Test
        fun `특정 유저의 아이디와 저장할 포인트를 받으면 domain 레이어에서 저장된 값을 받아온다`() {
            // given
            val userId = 1L
            val amount = 100L
            val initialUserPoint = UserPoint(id = userId, point = 1000L, updateMillis = System.currentTimeMillis())
            val userPoint = UserPoint(id = userId, point = 1100L, updateMillis = System.currentTimeMillis())
            val totalPointsAfterCharge = initialUserPoint.point + amount

            // when
            `when`(pointService.getUserPointById(userId)).thenReturn(initialUserPoint)
            `when`(point.chargePoint(userId = userId, amount = amount, totalPointsAfterCharge = totalPointsAfterCharge)).thenReturn(userPoint)
            val result = pointService.chargeUserPoint(userId, amount)

            // then
            assertThat(result).isEqualTo(userPoint)
        }

        @Test
        fun `저장하려는 포인트가 0보다 작은수가 전달되면 예외(IllegalArgumentException)를 발생 시킨다`() {
            val userId = 1L
            val amount = -100L

            assertThatThrownBy {
                pointService.chargeUserPoint(userId, amount)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("포인트는 0 이하일 수 없습니다")
        }
    }

    @Nested
    @DisplayName("[사용] 회원별 포인트 사용 서비스")
    inner class UseUserPointTests {

        @Test
        fun `특정 유저의 아이디와 사용할 포인트를 받으면 domain 레이어에서 사용하고 남은 값을 받아온다`() {
            // given
            val userId = 1L
            val amount = 100L
            val initialUserPoint = UserPoint(id = userId, point = 1000L, updateMillis = System.currentTimeMillis())
            val remainPoint = initialUserPoint.point - amount
            val remainUserPoint = UserPoint(id = userId, point = remainPoint, updateMillis = System.currentTimeMillis())

            // when
            `when`(pointService.getUserPointById(userId)).thenReturn(initialUserPoint)
            `when`(point.useUserPoint(userId, amount, remainPoint)).thenReturn(remainUserPoint)
            val result = pointService.useUserPoint(userId, amount)

            // then
            assertThat(result).isEqualTo(remainUserPoint)
        }

        @Test
        fun `사용하려는 포인트가 0 보다 작은수가 전달되면 예외(IllegalArgumentException)를 발생 시킨다`() {
            val userId = 1L
            val amount = -100L

            assertThatThrownBy {
                pointService.useUserPoint(userId, amount)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("포인트는 0 이하일 수 없습니다")
        }

        @Test
        fun `포인트 잔액이 부족할때 포인트 사용 요청이 들어오면 예외(NotEnoughPointException)를 발생 시킨다`() {
            val userId = 1L
            val amount = 100L
            val initialUserPoint = UserPoint(id = userId, point = 0, updateMillis = System.currentTimeMillis())

            `when`(pointService.getUserPointById(userId)).thenReturn(initialUserPoint)

            assertThatThrownBy {
                pointService.useUserPoint(userId, amount)
            }.isInstanceOf(NotEnoughPointException::class.java)
                .hasMessage("사용 가능한 포인트가 부족합니다")
        }
    }

    @Nested
    @DisplayName("[조회] 회원별 포인트 사용내역 조회 서비스")
    inner class GetUserPointHistoriesTests {

        @Test
        fun `특정 유저의 포인트 사용 내역을 조회한다`() {
            // given
            val userId = 1L
            val histories = listOf(
                PointHistory(1, userId, TransactionType.CHARGE, 100, 0),
                PointHistory(2, userId, TransactionType.USE, 10, 0)
            )

            // when
            `when`(point.getUserPointHistories(userId)).thenReturn(histories)
            val result = pointService.getUserPointHistories(userId)

            // then
            assertThat(result).isEqualTo(histories)
            assertThat(result).hasSize(2)
        }
    }
}