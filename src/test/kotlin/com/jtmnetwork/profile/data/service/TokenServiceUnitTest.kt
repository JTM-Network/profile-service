package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.entity.Token
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.token.TokenNotFound
import com.jtmnetwork.profile.core.usecase.provider.TokenProvider
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import com.jtmnetwork.profile.core.usecase.repository.TokenRepository
import com.jtmnetwork.profile.core.util.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class TokenServiceUnitTest {

    private val tokenRepository: TokenRepository = mock()
    private val tokenProvider: TokenProvider = mock()
    private val profileRepository: ProfileRepository = mock()
    private val tokenService = TokenService(tokenRepository, tokenProvider, profileRepository)

    private val profile = TestUtil.createProfile("id")
    private val headers = TestUtil.createHeaders("id")
    private val req = TestUtil.createRequest(headers)

    @Test
    fun generate_shouldThrowNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.generate(req)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun generate_shouldReturnToken() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(tokenProvider.createToken(anyString())).thenReturn("token")
        `when`(tokenRepository.save(anyOrNull())).thenReturn(Mono.just(Token("id", "token")))

        val returned = tokenService.generate(req)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isInstanceOf(String::class.java) }
            .verifyComplete()
    }

    @Test
    fun getToken_shouldThrowNotFound() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.getToken("id")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun getToken_shouldReturnToken() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(Token("id", "token")))

        val returned = tokenService.getToken("id")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo("token") }
            .verifyComplete()
    }

    @Test
    fun blacklistToken_shouldThrowNotFound() {}

    @Test
    fun blacklistToken_shouldReturnToken() {}
}