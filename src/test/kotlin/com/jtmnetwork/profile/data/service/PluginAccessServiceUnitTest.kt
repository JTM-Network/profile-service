package com.jtmnetwork.profile.data.service

import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedDeserialization
import com.jtmnetwork.profile.core.domain.exceptions.payment.InvalidPaymentIntent
import com.jtmnetwork.profile.core.domain.model.AccessDTO
import com.jtmnetwork.profile.core.usecase.stripe.StripeEventConstructor
import com.jtmnetwork.profile.core.usecase.stripe.StripeProcessor
import com.jtmnetwork.profile.core.util.TestUtil
import com.stripe.model.Event
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
class PluginAccessServiceUnitTest {

    private val permissionService: PermissionService = mock()
    private val processor: StripeProcessor<AccessDTO> = mock()
    private val constructor: StripeEventConstructor = mock()
    private val accessService = PluginAccessService(permissionService, processor, constructor)

    private val event: Event = mock()
    private val id = UUID.randomUUID().toString()
    private val created = TestUtil.createProfile(id)
    private val accessDTO = AccessDTO("id")

    private val headers = TestUtil.createStripeHeaders(id)
    private val request = TestUtil.createRequest(headers)

    @Test
    fun addHook_shouldFailedDeserialization() {
        `when`(constructor.construct(anyOrNull(), anyString())).thenReturn(Mono.just(event))
        `when`(processor.process(anyOrNull())).thenReturn(Mono.error(FailedDeserialization()))

        val returned = accessService.addHook(request, "body")

        verify(constructor, times(1)).construct(anyOrNull(), anyString())
        verifyNoMoreInteractions(constructor)

        StepVerifier.create(returned)
            .expectError(FailedDeserialization::class.java)
            .verify()
    }

    @Test
    fun addHook_shouldInvalidPayment() {
        `when`(constructor.construct(anyOrNull(), anyString())).thenReturn(Mono.just(event))
        `when`(processor.process(anyOrNull())).thenReturn(Mono.error(InvalidPaymentIntent()))

        val returned = accessService.addHook(request, "body")

        verify(constructor, times(1)).construct(anyOrNull(), anyString())
        verifyNoMoreInteractions(constructor)

        StepVerifier.create(returned)
            .expectError(InvalidPaymentIntent::class.java)
            .verify()
    }

    @Test
    fun addHook_shouldReturnProfile() {
        `when`(constructor.construct(anyOrNull(), anyString())).thenReturn(Mono.just(event))
        `when`(processor.process(anyOrNull())).thenReturn(Mono.just(accessDTO))
        `when`(permissionService.addPermissions(anyString(), anyOrNull())).thenReturn(Mono.just(created))

        val returned = accessService.addHook(request, "body")

        verify(constructor, times(1)).construct(anyOrNull(), anyString())
        verifyNoMoreInteractions(constructor)

        StepVerifier.create(returned)
            .assertNext {
                TestUtil.assertProfile(it, id)
            }
            .verifyComplete()
    }

    @Test
    fun addAccess_shouldReturnProfile() {
        `when`(permissionService.addPermission(anyOrNull())).thenReturn(Mono.just(created))

        val returned = accessService.addAccess("id", "perms")

        verify(permissionService, times(1)).addPermission(anyOrNull())
        verifyNoMoreInteractions(permissionService)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }

    @Test
    fun hasAccess_shouldComplete() {
        `when`(permissionService.hasPermission(anyString(), anyString())).thenReturn(Mono.empty())

        val returned = accessService.hasAccess("id", "perms")

        verify(permissionService, times(1)).hasPermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test
    fun removeAccess_shouldReturnProfile() {
        `when`(permissionService.removePermission(anyString(), anyString())).thenReturn(Mono.just(created))

        val returned = accessService.removeAccess("id", "perms")

        verify(permissionService, times(1)).removePermission(anyString(), anyString())
        verifyNoMoreInteractions(permissionService)

        StepVerifier.create(returned)
            .assertNext { TestUtil.assertProfile(it, id) }
            .verifyComplete()
    }
}