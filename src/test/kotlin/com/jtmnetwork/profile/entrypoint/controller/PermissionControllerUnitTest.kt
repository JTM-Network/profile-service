package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.exceptions.PermissionFound
import com.jtmnetwork.profile.core.domain.exceptions.PermissionNotFound
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.util.TestUtil
import com.jtmnetwork.profile.data.service.PermissionService
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
@WebFluxTest(PermissionController::class)
@AutoConfigureWebTestClient
class PermissionControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var permissionService: PermissionService

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val dto = TestUtil.createPermissionDTO(id)

    @Test
    fun postPermission_shouldThrowProfileNotFound() {
        `when`(permissionService.addPermission(anyOrNull())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.post()
            .uri("/permission")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(permissionService, times(1)).addPermission(anyOrNull())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun postPermission_shouldThrowPermissionFound() {
        `when`(permissionService.addPermission(anyOrNull())).thenReturn(Mono.error(PermissionFound()))

        testClient.post()
            .uri("/permission")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Permission already found.")

        verify(permissionService, times(1)).addPermission(anyOrNull())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun postPermission_shouldReturnProfile() {
        `when`(permissionService.addPermission(anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/permission")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(permissionService, times(1)).addPermission(anyOrNull())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun hasPermission_shouldThrowProfileNotFound() {
        `when`(permissionService.hasPermission(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.get()
            .uri("/permission/auth?id=$id&permission=perms")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(permissionService, times(1)).hasPermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun hasPermission_shouldThrowPermissionNotFound() {
        `when`(permissionService.hasPermission(anyString(), anyString())).thenReturn(Mono.error(PermissionNotFound()))

        testClient.get()
            .uri("/permission/auth?id=$id&permission=perms")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.message").isEqualTo("You do not have permission.")

        verify(permissionService, times(1)).hasPermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun hasPermission() {
        `when`(permissionService.hasPermission(anyString(), anyString())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/permission/auth?id=$id&permission=perms")
            .exchange()
            .expectStatus().isOk

        verify(permissionService, times(1)).hasPermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun deletePermission_shouldThrowProfileNotFound() {
        `when`(permissionService.removePermission(anyString(), anyString())).thenReturn(Mono.error(ProfileNotFound()))

        testClient.delete()
            .uri("/permission?id=$id&permission=perms")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(permissionService, times(1)).removePermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun deletePermission_shouldThrowPermissionNotFound() {
        `when`(permissionService.removePermission(anyString(), anyString())).thenReturn(Mono.error(PermissionNotFound()))

        testClient.delete()
            .uri("/permission?id=$id&permission=perms")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.message").isEqualTo("You do not have permission.")

        verify(permissionService, times(1)).removePermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }

    @Test
    fun deletePermission_shouldReturnProfile() {
        `when`(permissionService.removePermission(anyString(), anyString())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/permission?id=$id&permission=perms")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.status").isEqualTo(AccountStatus.ONLINE.toString())

        verify(permissionService, times(1)).removePermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)
    }
}