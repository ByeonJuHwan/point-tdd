package io.hhplus.tdd.application

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.internal.Ref.LongRef

@Component
class LockProvider {

    private val lockMap: ConcurrentMap<Long, ReentrantLock> = ConcurrentHashMap()

    fun getLock(key: Long): ReentrantLock = lockMap.computeIfAbsent(key) { ReentrantLock(true) }
}