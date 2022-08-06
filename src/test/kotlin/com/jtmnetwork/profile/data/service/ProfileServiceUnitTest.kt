package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.exceptions.InvalidRequestClientId
import com.jtmnetwork.profile.core.domain.exceptions.ProfileNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
import com.jtmnetwork.profile.core.util.TestUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class ProfileServiceUnitTest {

    private val profileRepository: ProfileRepository = mock()
    private val profileService = ProfileService(profileRepository)

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)

    private val headers = TestUtil.createHeaders("id")
    private val request = TestUtil.createRequest(headers)

    @Test
    fun getProfile_shouldThrowInvalid_whenHeaderNull() {
        val headers = TestUtil.createHeaders(null)
        val req = TestUtil.createRequest(headers)

        val returned = profileService.getProfile(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(InvalidRequestClientId::class.java)
            .verify()
    }

    @Test
    fun getProfile_shouldCreateProfile_whenEmpty() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = profileService.getProfile(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }

    @Test
    fun getProfile_shouldReturnProfile_whenFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = profileService.getProfile(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }

    @Test
    fun getProfileById_shouldThrowNotFound_whenNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = profileService.getProfileById("id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun getProfileById_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = profileService.getProfileById("id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }

    @Test
    fun banProfile_shouldThrowNotFound_whenNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = profileService.banProfile("id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun banProfile_shouldBanProfile_whenFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = profileService.banProfile("id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertBannedProfile(it, id) }
            .verifyComplete()
    }
}