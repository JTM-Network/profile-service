package com.jtmnetwork.profile.core.usecase.repository

import com.jtmnetwork.profile.core.domain.entity.Token
import com.jtmnetwork.profile.core.util.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
@DataMongoTest
class TokenRepositoryIntegrationTest {

    @Autowired
    lateinit var tokenRepository: TokenRepository

    private val id = UUID.randomUUID()
    private val created = TestUtil.createToken(id)

    @Before
    fun setup() {
        tokenRepository.deleteAll().block()
    }

    @Test
    fun save_shouldReturnToken() {
        val returned = tokenRepository.save(created).block()

        assertNotNull(returned)

        if (returned != null)  assertThat(returned.accountId).isEqualTo(id.toString())
    }

    @Test
    fun findById_shouldReturnToken() {
        tokenRepository.save(created).block()

        val returned = tokenRepository.findById(created.id)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.accountId).isEqualTo(id.toString()) }
            .verifyComplete()
    }

    @Test
    fun findById_shouldReturnEmpty() {
        val returned = tokenRepository.findById(id.toString())

        StepVerifier.create(returned)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun findAll_shouldReturnTokens() {
        tokenRepository.deleteAll().block()

        val secondId = UUID.randomUUID()

        tokenRepository.save(created).block()
        tokenRepository.save(Token(secondId.toString(), "test")).block()

        val returned = tokenRepository.findAll()

        StepVerifier.create(returned)
            .assertNext { assertThat(it.accountId).isEqualTo(id.toString()) }
            .assertNext { assertThat(it.accountId).isEqualTo(secondId.toString()) }
            .verifyComplete()
    }

    @Test
    fun findAll_shouldReturnEmpty() {
        tokenRepository.deleteAll().block()

        val returned = tokenRepository.findAll()

        StepVerifier.create(returned)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun deleteById_shouldReturnToken() {
        tokenRepository.save(created).block()

        val exists = tokenRepository.existsById(created.id).block()

        assertNotNull(exists)
        if (exists != null) assertTrue(exists)

        tokenRepository.deleteById(created.id).block()

        val returned = tokenRepository.existsById(created.id).block()

        assertNotNull(returned)
        if (returned != null) assertFalse(returned)
    }
}