package io.hhplus.tdd.infra

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.UserPoint

interface PointRepository {
    fun findById(userId: Long): UserPoint
    fun chargeUserPoint(userId: Long, amount: Long, totalPointsAfterCharge: Long): UserPoint
    fun useUserPoint(userId: Long, amount: Long, remainingPoints : Long): UserPoint
    fun getUserPointHistories(userId: Long): List<PointHistory>
}