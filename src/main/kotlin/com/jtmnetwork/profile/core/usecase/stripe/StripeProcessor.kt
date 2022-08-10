package com.jtmnetwork.profile.core.usecase.stripe

import com.stripe.model.Event
import reactor.core.publisher.Mono

interface StripeProcessor<T> {

    fun process(event: Event): Mono<T>
}