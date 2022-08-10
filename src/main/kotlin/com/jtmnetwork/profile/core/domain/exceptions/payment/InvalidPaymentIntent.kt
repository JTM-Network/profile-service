package com.jtmnetwork.profile.core.domain.exceptions.payment

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid payment intent given.")
class InvalidPaymentIntent: RuntimeException()