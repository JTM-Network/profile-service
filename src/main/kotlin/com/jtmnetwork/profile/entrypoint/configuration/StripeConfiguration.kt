package com.jtmnetwork.profile.entrypoint.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.jtmnetwork.profile.core.usecase.stripe.StripeEventDeserializer
import com.stripe.Stripe
import com.stripe.model.Event
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.annotation.PostConstruct

@Configuration
open class StripeConfiguration {

    private val log = LoggerFactory.getLogger(StripeConfiguration::class.java)

    @Value("\${stripe.secret-key}")
    lateinit var secretKey: String

    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
        log.info("Current stripe version: ${Stripe.API_VERSION}")
    }

    @Bean
    @Primary
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Event::class.java, StripeEventDeserializer(mapper))
        return mapper.registerModule(module)
    }
}