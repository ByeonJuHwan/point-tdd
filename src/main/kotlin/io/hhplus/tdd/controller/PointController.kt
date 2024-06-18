package io.hhplus.tdd.controller

import io.hhplus.tdd.application.PointService
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.UserPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPoint = pointService.getUserPointById(id)

    /** 
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요. 
     */ 
    @GetMapping("{id}/histories") 
    fun history( 
        @PathVariable id: Long, 
    ): List<PointHistory> = pointService.getUserPointHistories(id) 
 

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint = pointService.chargeUserPoint(id, amount)


    /** 
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요. 
     */ 
    @PatchMapping("{id}/use") 
    fun use( 
        @PathVariable id: Long, 
        @RequestBody amount: Long, 
    ): UserPoint = pointService.useUserPoint(id, amount) 
} 
 
