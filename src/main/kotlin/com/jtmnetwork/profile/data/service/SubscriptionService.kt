package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.dto.SubDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.exceptions.InvalidAge
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import com.jtmnetwork.profile.core.util.UtilTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SubscriptionService @Autowired constructor(private val profileRepository: ProfileRepository) {

    fun addSubscription(id: String, dto: SubDTO): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!UtilTime.validAge(dto.age) || UtilTime.parseAge(dto.age) <= 0) return@flatMap Mono.error(InvalidAge())
                if (it.hasSubscription(dto.name)) return@flatMap Mono.error(SubscriptionFound())
                profileRepository.save(it.addSubscription(dto.name, dto.level, dto.age))
            }
    }

    fun hasSubscription(id: String, name: String): Mono<Void> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.hasSubscription(name)) return@flatMap Mono.error(SubscriptionNotFound())
                Mono.empty()
            }
    }

    fun removeSubscription(id: String, name: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.hasSubscription(name)) return@flatMap Mono.error(SubscriptionNotFound())
                profileRepository.save(it.removeSubscription(name))
            }
    }
}