package io.hhplus.tdd.infra

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointRepositoryImplTest {

    @InjectMocks
    lateinit var pointRepositoryImpl: PointRepositoryImpl

    @Mock
    lateinit var pointHistoryTable: PointHistoryTable

    @Mock
    lateinit var pointHistoryStorage: PointHistoryStorage

    @Mock
    lateinit var userPointTable: UserPointTable

    @Test
    fun `특정 유저의 id를 받으면 userPointTable 에서 해당 유저의 포인트 정보를 반환한다`() {
        val userId = 1L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        `when`(userPointTable.selectById(userId)).thenReturn(userPoint)
        val result = userPointTable.selectById(userId)

        assertThat(result).isEqualTo(userPoint)
    }

    @Test
    fun `특정 유저의 id를 받으면 pointHistoryTable 에서 해당 유저의 포인트 사용 내역 리스트를 반환한다`() {
        val userId = 1L
        val histories = listOf(
            PointHistory(1, userId, TransactionType.CHARGE, 100, 0),
            PointHistory(2, userId, TransactionType.USE, 10, 0)
        )

        `when`(pointHistoryTable.selectAllByUserId(userId)).thenReturn(histories)
        val result = pointRepositoryImpl.getUserPointHistories(userId)

        assertThat(result).hasSize(2)
        assertThat(result[0].amount).isEqualTo(100)
        assertThat(result[1].amount).isEqualTo(10)
    }

    @Test
    fun `id, amount, totalPointsAfterCharge 를 전달 받고 테이블에서 포인트를 저장 후 저장 값을 반환해 준다`() {
        val userId = 1L
        val amount = 100L
        val totalPointsAfterCharge = 100L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        `when`(userPointTable.insertOrUpdate(userId, totalPointsAfterCharge)).thenReturn(userPoint)
        val result = pointRepositoryImpl.chargeUserPoint(userId, amount, totalPointsAfterCharge)

        assertThat(userPoint).isEqualTo(result)
    }

    @Test 
    fun `id, amount, remainingPoints 를 전달 받고 사용하고 남은 포인트를 테이블에 저장 후 저장 값을 반환해 준다 `() { 
        val userId = 1L 
        val amount = 50L 
        val remainingPoints = 50L 
        val userPoint = UserPoint(id = userId, point = remainingPoints, updateMillis = System.currentTimeMillis()) 
 
        `when`(userPointTable.insertOrUpdate(userId, remainingPoints)).thenReturn(userPoint) 
        val result = pointRepositoryImpl.useUserPoint(userId, amount, remainingPoints) 
 
        assertThat(userPoint).isEqualTo(result) 
    } 

    @Test
    fun `포인트를 충전 시 history 가 저장된다`() {
        val userId = 1L
        val amount = 100L
        val totalPointsAfterCharge = 100L
        val userPoint = UserPoint(id = userId, point = 100, updateMillis = System.currentTimeMillis())

        `when`(userPointTable.insertOrUpdate(userId, totalPointsAfterCharge)).thenReturn(userPoint)

        pointRepositoryImpl.chargeUserPoint(userId, amount, totalPointsAfterCharge)

        verify(pointHistoryStorage, times(1)).savePointHistory(userId, amount, TransactionType.CHARGE)
    }

    @Test 
    fun `포인트를 사용 시 history 가 저장된다`() { 
        val userId = 1L 
        val amount = 50L 
        val remainingPoints = 50L 
        val userPoint = UserPoint(id = userId, point = 50, updateMillis = System.currentTimeMillis()) 
 
        `when`(userPointTable.insertOrUpdate(userId, remainingPoints)).thenReturn(userPoint) 
 
        pointRepositoryImpl.useUserPoint(userId, amount, remainingPoints) 
 
        //verify(pointHistoryTable, times(1)).insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis()) 
        verify(pointHistoryStorage, times(1)).savePointHistory(userId, amount, TransactionType.USE) 
    } 
} 
