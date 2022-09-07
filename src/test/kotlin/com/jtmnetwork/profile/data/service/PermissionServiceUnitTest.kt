package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.exceptions.PermissionFound
import com.jtmnetwork.profile.core.domain.exceptions.PermissionNotFound
import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import com.jtmnetwork.profile.core.util.TestUtil
import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
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
import java.util.*

@RunWith(SpringRunner::class)
class PermissionServiceUnitTest {

    private val profileRepository: ProfileRepository = mock()
    private val permissionService = PermissionService(profileRepository)

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val dto = TestUtil.createPermissionDTO(id)

    @Test
    fun addPermission_shouldThrowProfileNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = permissionService.addPermission(dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun addPermission_shouldThrowPermissionFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addPermission("perms")))

        val returned = permissionService.addPermission(dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(PermissionFound::class.java)
            .verify()
    }

    @Test
    fun addPermission_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = permissionService.addPermission(dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                TestUtil.assertProfile(it, id)
                assertThat(it.status).isEqualTo(AccountStatus.ONLINE)
                assertTrue(it.hasPermission("perms"))
            }
            .verifyComplete()
    }

    @Test
    fun hasPermission_shouldThrowNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = permissionService.hasPermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun hasPermission_shouldThrowPermissionNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = permissionService.hasPermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(PermissionNotFound::class.java)
            .verify()
    }

    @Test
    fun hasPermission_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addPermission("perms")))

        val returned = permissionService.hasPermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test
    fun removePermission_shouldThrowProfileNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = permissionService.removePermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun removePermission_shouldThrowPermissionNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = permissionService.removePermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(PermissionNotFound::class.java)
            .verify()
    }

    @Test
    fun removePermission_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addPermission("perms")))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = permissionService.removePermission("id", "perms")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                TestUtil.assertProfile(it, id)
                assertThat(it.status).isEqualTo(AccountStatus.ONLINE)
                assertFalse(it.hasPermission("perms"))
            }
            .verifyComplete()
    }
}