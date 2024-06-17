package io.hhplus.tdd.infra

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.TransactionType
import org.springframework.stereotype.Component

@Component
class PointHistoryStorage (
    private val pointHistoryTable: PointHistoryTable,
) {
    fun savePointHistory(userId: Long, amount: Long, type : TransactionType) {
        pointHistoryTable.insert(
            id = userId,
            amount = amount,
            transactionType = type,
            updateMillis = System.currentTimeMillis()
        )
    }
}