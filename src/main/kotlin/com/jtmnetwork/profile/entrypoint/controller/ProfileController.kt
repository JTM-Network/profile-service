package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.ProfileInfoDto
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ProfileController @Autowired constructor(private val profileService: ProfileService) {

    @GetMapping("/me")
    fun getProfile(request: ServerHttpRequest): Mono<Profile> {
        return profileService.getProfile(request)
    }

    @PutMapping("/complete")
    fun updateProfile(request: ServerHttpRequest, @RequestBody dto: ProfileInfoDto): Mono<Profile> {
        return profileService.updateProfile(request, dto)
    }

    @GetMapping("/valid")
    fun validUsername(@RequestParam("username") username: String): Mono<Void> {
        return profileService.validUsername(username)
    }

    @GetMapping("/{id}")
    fun getProfileById(@PathVariable id: String): Mono<Profile> {
        return profileService.getProfileById(id)
    }

    @GetMapping("/all")
    fun getProfiles(): Flux<Profile> {
        return profileService.getProfiles()
    }

    @GetMapping("/ban/{id}")
    fun banProfile(@PathVariable id: String): Mono<Profile> {
        return profileService.banProfile(id)
    }

    @GetMapping("/unban/{id}")
    fun unbanProfile(@PathVariable id: String): Mono<Profile> {
        return profileService.unbanProfile(id)
    }
}