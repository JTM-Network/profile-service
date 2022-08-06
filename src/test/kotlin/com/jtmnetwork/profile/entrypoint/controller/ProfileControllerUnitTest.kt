package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.util.TestUtil
import com.jtmnetwork.profile.data.service.ProfileService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
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
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(ProfileController::class)
@AutoConfigureWebTestClient
class ProfileControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var profileService: ProfileService

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)

    @Test
    fun getProfile_shouldThrowInvalidRequest() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.error(InvalidRequestClientId()))

        testClient.get()
            .uri("/me")
            .exchange()
            .expectStatus().isBadRequest

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfile_shouldReturnProfile() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/me")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfileById_shouldThrowNotFound() {
        `when`(profileService.getProfileById(anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/${UUID.randomUUID().toString()}")
            .exchange()
            .expectStatus().isNotFound

        verify(profileService, times(1)).getProfileById(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfileById_shouldReturnProfile() {
        `when`(profileService.getProfileById(anyString())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/${UUID.randomUUID().toString()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(profileService, times(1)).getProfileById(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun banProfile_shouldThrowNotFound() {
        `when`(profileService.banProfile(anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/ban/${UUID.randomUUID().toString()}")
            .exchange()
            .expectStatus().isNotFound

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun banProfile_shouldReturnBannedProfile() {
        `when`(profileService.banProfile(anyString())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/ban/${UUID.randomUUID().toString()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }
}