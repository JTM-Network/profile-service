package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.data.service.TokenService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest(TokenController::class)
@AutoConfigureWebTestClient
class TokenControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var tokenService: TokenService

    @Test
    fun generate() {
        `when`(tokenService.generate(anyOrNull())).thenReturn(Mono.just("token"))

        testClient.get()
            .uri("/token/generate")
            .exchange()
            .expectStatus().isOk

        verify(tokenService, times(1)).generate(anyOrNull())
        verifyNoMoreInteractions(tokenService)
    }
}