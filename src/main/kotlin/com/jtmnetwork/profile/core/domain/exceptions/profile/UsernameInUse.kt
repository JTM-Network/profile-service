package com.jtmnetwork.profile.core.domain.exceptions.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Username is already in use.")
class UsernameInUse: RuntimeException()