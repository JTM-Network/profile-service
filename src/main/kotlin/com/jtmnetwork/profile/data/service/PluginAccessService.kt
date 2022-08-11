package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.model.AccessDTO
import com.jtmnetwork.profile.core.usecase.stripe.StripeProcessor
import com.stripe.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PluginAccessService @Autowired constructor(private val permissionService: PermissionService, @Qualifier("plugin_processor") private val processor: StripeProcessor<AccessDTO>) {

    private val prefix = "PLUGIN_"

    fun addHook(event: Event): Mono<Profile> {
        return processor.process(event)
            .flatMap { dto ->
                val permissions: Array<String> = dto.plugins.toList().map { prefix + it }.toTypedArray()
                permissionService.addPermissions(dto.id, permissions)
            }
    }

    fun addAccess(id: String, permission: String): Mono<Profile> {
        return permissionService.addPermission(PermissionDTO(id, prefix + permission))
    }

    fun hasAccess(id: String, permission: String): Mono<Void> {
        return permissionService.hasPermission(id, prefix + permission)
    }

    fun removeAccess(id: String, permission: String): Mono<Profile> {
        return permissionService.removePermission(id, prefix + permission)
    }
}