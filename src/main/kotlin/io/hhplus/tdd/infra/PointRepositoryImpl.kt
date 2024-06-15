package io.hhplus.tdd.infra

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Repository

@Repository
class PointRepositoryImpl(
    private val userPointTable: UserPointTable,
) : PointRepository {

    override fun findById(userId: Long): UserPoint {
        return userPointTable.selectById(id = userId)
    }

    override fun chargeUserPoint(userId: Long, amount: Long): UserPoint {
        return userPointTable.insertOrUpdate(id = userId, amount = amount)
    }

    override fun userUserPoint(userId: Long, amount: Long): UserPoint {
        return userPointTable.insertOrUpdate(userId, amount)
    }
}