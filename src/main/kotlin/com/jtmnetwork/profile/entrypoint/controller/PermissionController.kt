package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.data.service.PermissionService
import com.stripe.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/permission")
class PermissionController @Autowired constructor(private val permissionService: PermissionService) {

    @PostMapping
    fun postPermission(@RequestBody dto: PermissionDTO): Mono<Profile> = permissionService.addPermission(dto)

    @GetMapping("/auth")
    fun hasPermission(@RequestParam("id") id: String, @RequestParam("permission") permission: String): Mono<Void> = permissionService.hasPermission(id, permission)

    @DeleteMapping
    fun deletePermission(@RequestParam("id") id: String, @RequestParam("permission") permission: String): Mono<Profile> = permissionService.removePermission(id, permission)
}