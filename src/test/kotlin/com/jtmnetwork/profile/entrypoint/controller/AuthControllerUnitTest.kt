package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.AuthDTO
import com.jtmnetwork.profile.data.service.AuthService
import com.jtmnetwork.profile.data.service.TokenService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
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
@WebFluxTest(AuthController::class)
@AutoConfigureWebTestClient
class AuthControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var authService: AuthService

    @MockBean
    lateinit var tokenService: TokenService

    private val dto = AuthDTO("id", "plugin")

    @Test
    fun authenticate() {
        `when`(authService.authenticate(anyString(), anyString())).thenReturn(Mono.empty())

        testClient.post()
            .uri("/auth")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk

        verify(authService, times(1)).authenticate(anyString(), anyString())
        verifyNoMoreInteractions(authService)
    }

    @Test
    fun getToken() {
        `when`(tokenService.getToken(anyString())).thenReturn(Mono.just("token"))

        testClient.get()
            .uri("/auth/id")
            .exchange()
            .expectStatus().isOk

        verify(tokenService, times(1)).getToken(anyString())
        verifyNoMoreInteractions(tokenService)
    }
}