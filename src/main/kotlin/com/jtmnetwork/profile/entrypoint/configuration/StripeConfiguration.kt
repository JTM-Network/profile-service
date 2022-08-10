package com.jtmnetwork.profile.entrypoint.configuration

import com.stripe.Stripe
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
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
}