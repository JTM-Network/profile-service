package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.entity.Token
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.InvalidToken
import com.jtmnetwork.profile.core.domain.exceptions.PermissionNotFound
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.provider.TokenProvider
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
import java.util.*

@RunWith(SpringRunner::class)
class AuthServiceUnitTest {

    private val tokenRepository: TokenRepository = mock()
    private val tokenProvider: TokenProvider = mock()
    private val accessService: PluginAccessService = mock()
    private val authService = AuthService(tokenRepository, tokenProvider, accessService)

    private val token = Token("accountId", "token")
    private val profile = TestUtil.createProfile("id")

    private val headers = TestUtil.createHeaders("id")
    private val req = TestUtil.createRequest(headers)

    @Test
    fun authenticate_shouldThrowInvalidRequest() {
        val header = TestUtil.createStripeHeaders("test")
        val request = TestUtil.createRequest(header)

        val returned = authService.authenticate(request, "plugin")

        verify(header, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(header)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        StepVerifier.create(returned)
            .expectError(InvalidRequestClientId::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowInvalidToken() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("token")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(Optional.empty())

        val returned = authService.authenticate(req, "plugin")

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidToken::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowTokenNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("token")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(Optional.of("id"))
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.empty())

        val returned = authService.authenticate(req, "plugin")

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowProfileNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("token")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(Optional.of("id"))
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        val returned = authService.authenticate(req, "plugin")

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldThrowNotAuthenticated() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("token")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(Optional.of("id"))
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.error(PermissionNotFound()))

        val returned = authService.authenticate(req, "plugin")

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(PermissionNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_shouldSucceed() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("token")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(Optional.of("id"))
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.just(token))
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.empty())

        val returned = authService.authenticate(req, "plugin")

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}