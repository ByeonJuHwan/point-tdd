package io.hhplus.tdd.domain

import io.hhplus.tdd.infra.PointRepository
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service

@Service
class Point(
    private val pointRepository: PointRepository,
) {
    fun getUserPointById(userId: Long): UserPoint{
        return pointRepository.findById(userId)
    }

    fun chargePoint(userId: Long, amount: Long): UserPoint {
        return pointRepository.chargeUserPoint(userId, amount)
    }

    fun useUserPoint(userId: Long, amount: Long): UserPoint {
        return pointRepository.userUserPoint(userId, amount)
    }
}