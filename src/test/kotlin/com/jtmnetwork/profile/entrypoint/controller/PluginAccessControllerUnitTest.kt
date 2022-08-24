package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.util.TestUtil
import com.jtmnetwork.profile.data.service.PluginAccessService
import com.stripe.model.Event
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
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
@WebFluxTest(PluginAccessController::class)
@AutoConfigureWebTestClient
class PluginAccessControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var accessService: PluginAccessService

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val dto = TestUtil.createPermissionDTO(id)
    private val event: Event = mock()

    @Test
    fun postHook_shouldReturnProfile() {
        `when`(accessService.addHook(anyOrNull(), anyString())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/plugin/access/hook")
            .bodyValue(event)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(accessService, times(1)).addHook(anyOrNull(), anyString())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun postAccess_shouldReturnProfile() {
        `when`(accessService.addAccess(anyString(), anyString())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/plugin/access")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(accessService, times(1)).addAccess(anyString(), anyString())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun getAccess_shouldComplete() {
        `when`(accessService.hasAccess(anyString(), anyString())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/plugin/access/auth?id=$id&permission=test")
            .exchange()
            .expectStatus().isOk

        verify(accessService, times(1)).hasAccess(anyString(), anyString())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun deleteAccess_ShouldReturnProfile() {
        `when`(accessService.removeAccess(anyString(), anyString())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/plugin/access?id=$id&permission=test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(accessService, times(1)).removeAccess(anyString(), anyString())
        verifyNoMoreInteractions(accessService)
    }
}