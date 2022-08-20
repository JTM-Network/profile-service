package com.jtmnetwork.profile.core.usecase.stripe

import com.google.gson.JsonSyntaxException
import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedJson
import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedSignature
import com.jtmnetwork.profile.core.util.UtilRequests
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.net.Webhook
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono

class StripeEventConstructor {

    fun construct(request: ServerHttpRequest): Mono<Event> {
        val sig = request.headers.getFirst("Stripe-Signature")
        val secret = request.headers.getFirst("Stripe-Secret")
        val body = UtilRequests.getBodyAsString(request)
        return body.flatMap {
            try {
                val event = Webhook.constructEvent(it, sig, secret)
                return@flatMap Mono.just(event)
            } catch (ex: JsonSyntaxException) {
                return@flatMap Mono.error(FailedJson())
            } catch (ex: SignatureVerificationException) {
                return@flatMap Mono.error(FailedSignature())
            }
        }
    }
}