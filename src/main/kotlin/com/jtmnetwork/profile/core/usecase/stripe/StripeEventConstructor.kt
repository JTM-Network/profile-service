package com.jtmnetwork.profile.core.usecase.stripe

import com.google.gson.JsonSyntaxException
import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedJson
import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedSignature
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.net.Webhook
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono

class StripeEventConstructor {

    fun construct(request: ServerHttpRequest, body: String): Mono<Event> {
        val sig = request.headers.getFirst("Stripe-Signature")
        val secret = request.headers.getFirst("Stripe-Secret")
        return try {
            val event = Webhook.constructEvent(body, sig, secret)
            Mono.just(event)
        } catch (ex: JsonSyntaxException) {
            Mono.error(FailedJson())
        } catch (ex: SignatureVerificationException) {
            Mono.error(FailedSignature())
        }
    }
}