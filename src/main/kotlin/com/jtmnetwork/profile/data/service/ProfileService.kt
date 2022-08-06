package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProfileService @Autowired constructor(private val profileRepository: ProfileRepository) {

    fun getProfile(request: ServerHttpRequest): Mono<Profile> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.error(InvalidRequestClientId())
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { profileRepository.save(Profile(id)) })
    }

    fun getProfileById(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
    }

    fun banProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.save(it.ban()) }
    }
}