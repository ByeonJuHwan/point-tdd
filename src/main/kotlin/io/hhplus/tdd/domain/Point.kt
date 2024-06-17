package io.hhplus.tdd.domain

import io.hhplus.tdd.infra.PointRepository
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service

@Service
class Point(
    private val pointRepository: PointRepository,
) {
    fun getUserPointById(userId: Long): UserPoint{
        return pointRepository.findById(userId)
    }

    fun getUserPointHistories(userId: Long): List<PointHistory> {
        return pointRepository.getUserPointHistories(userId)
    }

    fun chargePoint(userId: Long, amount: Long, totalPointsAfterCharge : Long): UserPoint {
        return pointRepository.chargeUserPoint(userId, amount, totalPointsAfterCharge)
    }

    fun useUserPoint(userId: Long, amount : Long ,remainingPoints: Long): UserPoint {
        return pointRepository.useUserPoint(userId, amount, remainingPoints)
    }
}