package io.hhplus.tdd.application

import io.hhplus.tdd.domain.Point
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service

@Service
class PointService(
    private val point: Point,
) {
    fun getUserPointById(id: Long) : UserPoint {
        return point.getUserPointById(id)
    }

    fun chargeUserPoint(userId: Long, amount: Long): UserPoint {
        return point.chargePoint(userId, amount)
    }

    fun useUserPoint(userId: Long, amount: Long): UserPoint {
        val userPointById = getUserPointById(id = userId)
        // TODO validation check 를 해줘야함 음수 등등 포인트 부족시 에러 등등
        val remainPoint = userPointById.point - amount
        return point.useUserPoint(userId, remainPoint)
    }
}