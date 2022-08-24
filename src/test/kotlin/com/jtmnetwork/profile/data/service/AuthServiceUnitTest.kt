package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.entity.Token
import com.jtmnetwork.profile.core.domain.exceptions.PermissionNotFound
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.repository.TokenRepository
import com.jtmnetwork.profile.core.util.TestUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class AuthServiceUnitTest {

    private val tokenRepository: TokenRepository = mock()
    private val accessService: PluginAccessService = mock()
    private val authService = AuthService(tokenRepository, accessService)

    private val token = Token("accountId", "token")
    private val profile = TestUtil.createProfile("id")

    @Test
    fun authenticate_shouldThrowTokenNotFound() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = authService.authenticate("id", "plugin")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowProfileNotFound() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        val returned = authService.authenticate("id", "plugin")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowNotAuthenticated() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.error(PermissionNotFound()))

        val returned = authService.authenticate("id", "plugin")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(PermissionNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldSucceed() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.empty())

        val returned = authService.authenticate("id", "plugin")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}