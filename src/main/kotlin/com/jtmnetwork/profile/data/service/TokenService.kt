package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.entity.Token
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.provider.TokenProvider
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import com.jtmnetwork.profile.core.usecase.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TokenService @Autowired constructor(private val tokenRepository: TokenRepository, private val tokenProvider: TokenProvider, private val profileRepository: ProfileRepository) {

    fun generate(req: ServerHttpRequest): Mono<String> {
        val clientId = req.headers.getFirst("CLIENT_ID") ?: return Mono.error(InvalidRequestClientId())
        return profileRepository.findById(clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { tokenRepository.save(Token(it.id, tokenProvider.createToken(it.id))).map { token -> token.id } }
    }

    fun getToken(id: String): Mono<String> {
        return tokenRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
            .map { it.token }
    }

    fun getTokensByAccountId(id: String): Flux<Token> {
        return Flux.empty()
    }

    fun blacklistToken(req: ServerHttpRequest): Mono<String> {
        return Mono.empty()
    }
}