package io.hhplus.tdd.infra

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.domain.Point
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

// TODO 나머지 테스트 작성
@ExtendWith(MockitoExtension::class)
class PointRepositoryImplTest {

    @InjectMocks
    lateinit var pointRepositoryImpl: PointRepositoryImpl

    @Mock
    lateinit var pointHistoryTable: PointHistoryTable

    @Mock
    lateinit var userPointTable: UserPointTable

    @Test
    fun findById() {
    }

    @Test
    fun getUserPointHistories() {
    }

    @Test
    fun `id, amount, totalPointsAfterCharge 를 전달 받고 포인트 충전 내역에 저장 후 테이블에서 값을 가져와서 반환해 준다`() {
        val userId = 1L
        val amount = 100L
        val totalPointsAfterCharge = 100L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        `when`(userPointTable.insertOrUpdate(userId, totalPointsAfterCharge)).thenReturn(userPoint)
        val result = pointRepositoryImpl.chargeUserPoint(userId, amount, totalPointsAfterCharge)

        assertThat(userPoint).isEqualTo(result)
    }

    @Test
    fun userUserPoint() {
    }
}