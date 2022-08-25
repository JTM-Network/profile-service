package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.InvalidToken
import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.provider.TokenProvider
import com.jtmnetwork.profile.core.usecase.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService @Autowired constructor(private val tokenRepository: TokenRepository, private val tokenProvider: TokenProvider, private val accessService: PluginAccessService) {

    fun authenticate(req: ServerHttpRequest, plugin: String): Mono<Void> {
        val bearer = req.headers.getFirst("PLUGIN_AUTHORIZATION") ?: return Mono.error(InvalidRequestClientId())
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token)
        if (accountId.isEmpty) return Mono.error(InvalidToken())
        return tokenRepository.findByToken(token)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
            .flatMap { accessService.hasAccess(accountId.get(), plugin) }
    }
}