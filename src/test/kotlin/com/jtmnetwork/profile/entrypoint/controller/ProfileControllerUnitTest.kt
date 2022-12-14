package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.dto.ProfileInfoDto
import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileBanned
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileUnbanned
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
    private val dto = ProfileInfoDto("test", 1990, 5, 30)

    @Test
    fun getProfile_shouldThrowInvalidRequest() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.error(InvalidRequestClientId()))

        testClient.get()
            .uri("/me")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.message").isEqualTo("CLIENT_ID header missing.")

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
    fun updateProfile() {
        `when`(profileService.updateProfile(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/complete")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(profileService, times(1)).updateProfile(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun validUsername() {
        `when`(profileService.validUsername(anyString())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/valid?username=test")
            .exchange()
            .expectStatus().isOk

        verify(profileService, times(1)).validUsername(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfileById_shouldThrowNotFound() {
        `when`(profileService.getProfileById(anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(profileService, times(1)).getProfileById(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfileById_shouldReturnProfile() {
        `when`(profileService.getProfileById(anyString())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/${UUID.randomUUID()}")
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
            .uri("/ban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun banProfile_shouldThrowBanned() {
        `when`(profileService.banProfile(anyString())).thenReturn(Mono.error(ProfileBanned()))

        testClient.get()
            .uri("/ban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile is already banned.")

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun banProfile_shouldReturnBannedProfile() {
        `when`(profileService.banProfile(anyString())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/ban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun unbanProfile_shouldThrowNotFound() {
        `when`(profileService.unbanProfile(anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/unban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(profileService, times(1)).unbanProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun unbanProfile_shouldThrowUnbanned() {
        `when`(profileService.unbanProfile(anyString())).thenReturn(Mono.error(ProfileUnbanned()))

        testClient.get()
            .uri("/unban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile is already unbanned.")

        verify(profileService, times(1)).unbanProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun unbanProfile_shouldReturnUnbannedProfile() {
        `when`(profileService.unbanProfile(anyString())).thenReturn(Mono.just(created.unban()))

        testClient.get()
            .uri("/unban/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.OFFLINE.toString())

        verify(profileService, times(1)).unbanProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }
}