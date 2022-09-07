package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.exceptions.profile.ProfileNotFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionFound
import com.jtmnetwork.profile.core.domain.exceptions.SubscriptionNotFound
import com.jtmnetwork.profile.core.usecase.repository.ProfileRepository
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
import java.util.*

@RunWith(SpringRunner::class)
class SubscriptionServiceUnitTest {

    private val profileRepository: ProfileRepository = mock()
    private val subscriptionService = SubscriptionService(profileRepository)

    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val dto = TestUtil.createSubDTO()

    @Test
    fun addSubscription_shouldThrowNotFound_whenEmpty() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = subscriptionService.addSubscription("id", dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun addSubscription_shouldThrowSubFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addSubscription(dto.name, dto.level, dto.age)))

        val returned = subscriptionService.addSubscription("id", dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(SubscriptionFound::class.java)
            .verify()
    }

    @Test
    fun addSubscription_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = subscriptionService.addSubscription("id", dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                TestUtil.assertProfile(it, id)
                assertThat(it.subs.size).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun hasSubscription_shouldThrowNotFound_whenEmpty() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = subscriptionService.hasSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun hasSubscription_shouldSubNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = subscriptionService.hasSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(SubscriptionNotFound::class.java)
            .verify()
    }

    @Test
    fun hasSubscription_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addSubscription(dto.name, dto.level, dto.age)))

        val returned = subscriptionService.hasSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test
    fun removeSubscription_shouldThrowNotFound_whenEmpty() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = subscriptionService.removeSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun removeSubscription_shouldThrowSubNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = subscriptionService.removeSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(SubscriptionNotFound::class.java)
            .verify()
    }

    @Test
    fun removeSubscription_shouldReturnProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(created.addSubscription(dto.name, dto.level, dto.age)))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = subscriptionService.removeSubscription("id", "plugin")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }
}