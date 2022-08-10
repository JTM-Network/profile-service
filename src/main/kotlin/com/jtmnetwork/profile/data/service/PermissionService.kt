package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.exceptions.PermissionFound
import com.jtmnetwork.profile.core.domain.exceptions.PermissionNotFound
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PermissionService @Autowired constructor(private val profileRepository: ProfileRepository) {

    fun addPermission(dto: PermissionDTO): Mono<Profile> {
        return profileRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (it.hasPermission(dto.permission)) return@flatMap Mono.error(PermissionFound())
                profileRepository.save(it.addPermission(dto.permission))
            }
    }

    fun hasPermission(id: String, permission: String): Mono<Void> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.hasPermission(permission)) return@flatMap Mono.error(PermissionNotFound())
                Mono.empty()
            }
    }

    fun removePermission(id: String, permission: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.hasPermission(permission)) return@flatMap Mono.error(PermissionNotFound())
                profileRepository.save(it.removePermission(permission))
            }
    }
}