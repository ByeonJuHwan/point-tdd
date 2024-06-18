package io.hhplus.tdd.infra

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Repository

@Repository
class PointRepositoryImpl(
    private val userPointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable,
    private val pointHistoryStorage: PointHistoryStorage, 
) : PointRepository {

    override fun findById(userId: Long): UserPoint {
        return userPointTable.selectById(userId)
    }

    override fun getUserPointHistories(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }
 
    override fun chargeUserPoint(userId: Long, amount: Long, totalPointsAfterCharge : Long): UserPoint { 
//        savePointHistory(userId, amount, type = TransactionType.CHARGE) 
        pointHistoryStorage.savePointHistory(userId, amount, type = TransactionType.CHARGE) 
        return userPointTable.insertOrUpdate(id = userId, amount = totalPointsAfterCharge) 
    } 

    override fun useUserPoint(userId: Long, amount: Long, remainingPoints : Long): UserPoint {
//        savePointHistory(userId, amount, type = TransactionType.USE)
        pointHistoryStorage.savePointHistory(userId, amount, type = TransactionType.USE)
        return userPointTable.insertOrUpdate(userId, remainingPoints)
    }
 
    private fun savePointHistory(userId: Long, amount: Long, type : TransactionType) { 
        pointHistoryTable.insert( 
            id = userId, 
            amount = amount, 
            transactionType = type, 
            updateMillis = System.currentTimeMillis() 
        ) 
    } 
}
