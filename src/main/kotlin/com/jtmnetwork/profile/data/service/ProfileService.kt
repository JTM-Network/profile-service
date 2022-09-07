package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.dto.ProfileInfoDto
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileBanned
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileUnbanned
import com.jtmnetwork.profile.core.domain.exceptions.profile.UsernameInUse
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProfileService @Autowired constructor(private val profileRepository: ProfileRepository) {

    /**
     * Fetches the profile using the request headers "CLIENT_ID" value, if the profile is not found
     * pertaining to the id, create a new profile.
     *
     * @param request                   the http request.
     * @return                          the profile found.
     * @throws InvalidRequestClientId   if the CLIENT_ID header is not found.
     */
    fun getProfile(request: ServerHttpRequest): Mono<Profile> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.error(InvalidRequestClientId())
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { profileRepository.save(Profile(id)) })
    }

    fun updateProfile(request: ServerHttpRequest, dto: ProfileInfoDto): Mono<Profile> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.error(InvalidRequestClientId())
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profile -> profileRepository.findByInfo_Username(dto.username)
                .flatMap<Profile> { Mono.error(UsernameInUse()) }
                .switchIfEmpty(Mono.defer { profileRepository.save(profile.updateProfile(dto)) })
            }
    }

    fun validUsername(username: String): Mono<Void> {
        return profileRepository.findByInfo_Username(username)
            .flatMap { Mono.error(UsernameInUse()) }
    }

    /**
     * Fetches the profile using the identifier.
     *
     * @param id                        the identifier.
     * @return                          the profile found.
     * @throws ProfileNotFound          if the profile is not found.
     */
    fun getProfileById(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
    }

    /**
     * Fetches all the profiles.
     *
     * @see                             Profile
     * @return                          Listed profiles
     */
    fun getProfiles(): Flux<Profile> {
        return profileRepository.findAll()
    }

    /**
     * Bans the profile using the identifier. If profile is already banned throw
     * exception.
     *
     * @param id                        the identifier.
     * @return                          the banned profile.
     * @throws ProfileNotFound          if the profile is not found.
     * @throws ProfileBanned            if the profile is already banned.
     */
    fun banProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (it.isBanned()) return@flatMap Mono.error(ProfileBanned())
                profileRepository.save(it.ban())
            }
    }

    /**
     * Unbans the profile using the identifier. If profile is not banned throw
     * exception.
     *
     * @param id                        the identifier.
     * @return                          the unbanned profile.
     * @throws ProfileNotFound          if the profile is not found.
     * @throws ProfileUnbanned          if the profile is unbanned.
     */
    fun unbanProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.isBanned()) return@flatMap Mono.error(ProfileUnbanned())
                profileRepository.save(it.unban())
            }
    }
}