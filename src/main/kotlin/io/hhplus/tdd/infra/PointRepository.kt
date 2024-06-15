package io.hhplus.tdd.infra

import io.hhplus.tdd.point.UserPoint

interface PointRepository {
    fun findById(userId: Long): UserPoint
    fun chargeUserPoint(userId: Long, amount: Long): UserPoint
    fun userUserPoint(userId: Long, amount: Long): UserPoint
}