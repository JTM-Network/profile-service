package com.jtmnetwork.profile.core.usecase.repository

import com.jtmnetwork.profile.core.domain.entity.Token
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TokenRepository: ReactiveMongoRepository<Token, String> {

    fun findByAccountId(accountId: String): Flux<Token>

}