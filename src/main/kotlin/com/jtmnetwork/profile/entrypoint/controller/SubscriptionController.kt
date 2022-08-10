package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.SubDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.model.Subscription
import com.jtmnetwork.profile.data.service.SubscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/sub")
class SubscriptionController @Autowired constructor(private val subscriptionService: SubscriptionService) {

    @PostMapping("/create")
    fun postSubscription(@RequestParam("id") id: String, @RequestBody dto: SubDTO): Mono<Profile> = subscriptionService.addSubscription(id, dto)

    @GetMapping("/check")
    fun checkSubscription(@RequestParam("id") id: String, @RequestParam("name") name: String): Mono<Void> = subscriptionService.hasSubscription(id, name)

    @DeleteMapping
    fun deleteSubscription(@RequestParam("id") id: String, @RequestParam("name") name: String): Mono<Profile> = subscriptionService.removeSubscription(id, name)
}