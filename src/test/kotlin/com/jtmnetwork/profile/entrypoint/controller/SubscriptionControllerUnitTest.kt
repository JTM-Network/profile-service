package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionNotFound
import com.jtmnetwork.profile.core.util.TestUtil
import com.jtmnetwork.profile.data.service.SubscriptionService
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
@WebFluxTest(SubscriptionController::class)
@AutoConfigureWebTestClient
class SubscriptionControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var subscriptionService: SubscriptionService

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val dto = TestUtil.createSubDTO()

    @Test
    fun postSubscription_shouldProfileNotFound() {
        `when`(subscriptionService.addSubscription(anyString(), anyOrNull())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.post()
            .uri("/sub/create?id=${id}")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(subscriptionService, times(1)).addSubscription(anyString(), anyOrNull())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun postSubscription_shouldSubscriptionFound() {
        `when`(subscriptionService.addSubscription(anyString(), anyOrNull())).thenReturn(Mono.error(SubscriptionFound()))

        testClient.post()
            .uri("/sub/create?id=$id")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Subscription already found.")

        verify(subscriptionService, times(1)).addSubscription(anyString(), anyOrNull())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun postSubscription() {
        `when`(subscriptionService.addSubscription(anyString(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/sub/create?id=$id")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(subscriptionService, times(1)).addSubscription(anyString(), anyOrNull())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun checkSubscription_shouldProfileNotFound() {
        `when`(subscriptionService.hasSubscription(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/sub/check?id=$id&name=test")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(subscriptionService, times(1)).hasSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun checkSubscription_shouldSubscriptionNotFound() {
        `when`(subscriptionService.hasSubscription(anyString(), anyString())).thenReturn(Mono.error(SubscriptionNotFound()))

        testClient.get()
            .uri("/sub/check?id=$id&name=test")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Subscription not found.")

        verify(subscriptionService, times(1)).hasSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun checkSubscription() {
        `when`(subscriptionService.hasSubscription(anyString(), anyString())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/sub/check?id=$id&name=test")
            .exchange()
            .expectStatus().isOk

        verify(subscriptionService, times(1)).hasSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun removeSubscription_shouldProfileNotFound() {
        `when`(subscriptionService.removeSubscription(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.delete()
            .uri("/sub?id=$id&name=test")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(subscriptionService, times(1)).removeSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun removeSubscription_shouldSubscriptionNotFound() {
        `when`(subscriptionService.removeSubscription(anyString(), anyString())).thenReturn(Mono.error(SubscriptionNotFound()))

        testClient.delete()
            .uri("/sub?id=$id&name=test")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Subscription not found.")

        verify(subscriptionService, times(1)).removeSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }

    @Test
    fun removeSubscription() {
        `when`(subscriptionService.removeSubscription(anyString(), anyString())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/sub?id=$id&name=test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(subscriptionService, times(1)).removeSubscription(anyString(), anyString())
        verifyNoMoreInteractions(subscriptionService)
    }
}