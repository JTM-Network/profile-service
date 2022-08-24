package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.data.service.PluginAccessService
import com.stripe.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/plugin/access")
class PluginAccessController @Autowired constructor(private val accessService: PluginAccessService) {

    @PostMapping("/hook")
    fun postHook(request: ServerHttpRequest, @RequestBody data: String): Mono<Profile> = accessService.addHook(request, data)

    @PostMapping
    fun postAccess(@RequestBody dto: PermissionDTO): Mono<Profile> = accessService.addAccess(dto.id, dto.permission)

    @GetMapping("/auth")
    fun getAccess(@RequestParam("id") id: String, @RequestParam("permission") permission: String): Mono<Void> = accessService.hasAccess(id, permission)

    @DeleteMapping
    fun deleteAccess(@RequestParam("id") id: String, @RequestParam("permission") permission: String): Mono<Profile> = accessService.removeAccess(id, permission)
}