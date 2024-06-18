package io.hhplus.tdd.application

import io.hhplus.tdd.domain.Point
import io.hhplus.tdd.exception.NotEnoughPointException
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service

@Service
class PointService(
    private val point: Point,
) {
    fun getUserPointById(userId: Long) : UserPoint {
        return point.getUserPointById(userId)
    }
 
    @Synchronized 
    fun chargeUserPoint(userId: Long, amount: Long): UserPoint { 
        validateAmount(amount) 
        val currentUserPoint = getUserPointById(userId) 
        return updateUserPointForCharge(userId, amount, currentUserPoint) 
    } 
 
    @Synchronized
    fun useUserPoint(userId: Long, amount: Long): UserPoint {
        validateAmount(amount)
        val currentUserPoint = getUserPointById(userId)
        validateSufficientPoints(currentUserPoint, amount)
        return updateUserPointForUse(userId, amount, currentUserPoint)
    }

    fun getUserPointHistories(userId: Long): List<PointHistory> {
        return point.getUserPointHistories(userId)
    }

 
    private fun validateAmount(amount: Long) { 
        if (amount <= 0) throw IllegalArgumentException("포인트는 0 이하일 수 없습니다") 
    } 
 
    private fun validateSufficientPoints(userPointById: UserPoint, amount: Long) { 
        if (userPointById.point < amount) throw NotEnoughPointException("사용 가능한 포인트가 부족합니다") 
    } 
 
    private fun updateUserPointForCharge(userId: Long, amount: Long, currentUserPoint: UserPoint): UserPoint { 
        val totalPointsAfterCharge = currentUserPoint.point + amount 
        return point.chargePoint(userId, amount, totalPointsAfterCharge) 
    } 
 
    private fun updateUserPointForUse(userId: Long, amount: Long, currentUserPoint: UserPoint): UserPoint {
        val remainingPoints = currentUserPoint.point - amount
        return point.useUserPoint(userId, amount, remainingPoints)
    }
}
