package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService @Autowired constructor(private val tokenRepository: TokenRepository, private val accessService: PluginAccessService) {

    fun authenticate(id: String, plugin: String): Mono<Void> {
        return tokenRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
            .flatMap { accessService.hasAccess(id, plugin) }
    }
}