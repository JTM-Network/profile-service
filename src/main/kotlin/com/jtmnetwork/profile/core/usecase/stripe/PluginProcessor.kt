package com.jtmnetwork.profile.core.usecase.stripe

import com.jtmnetwork.profile.core.domain.exceptions.payment.FailedDeserialization
import com.jtmnetwork.profile.core.domain.exceptions.payment.InvalidPaymentIntent
import com.jtmnetwork.profile.core.domain.model.AccessDTO
import com.jtmnetwork.profile.core.util.UtilString
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.model.StripeObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("plugin_processor")
class PluginProcessor: StripeProcessor<AccessDTO> {

    private val logger = LoggerFactory.getLogger(PluginProcessor::class.java)

    override fun process(event: Event): Mono<AccessDTO> {
        val dataObjectDeserializer = event.dataObjectDeserializer
        if (!dataObjectDeserializer.`object`.isPresent) return Mono.error { FailedDeserialization() }
        val stripeObject: StripeObject = dataObjectDeserializer.`object`.get()
        val intent: PaymentIntent = stripeObject as PaymentIntent
        val accountId = intent.metadata["accountId"] ?: return Mono.error { InvalidPaymentIntent() }
        val plugins = intent.metadata["plugins"] ?: return Mono.error { InvalidPaymentIntent() }
        logger.info("Plugins found: $plugins")
        val pluginIds = UtilString.stringToPlugins(plugins)
        logger.info("Ids: $pluginIds")
        val dto = AccessDTO(accountId, pluginIds)
        return Mono.just(dto)
    }
}