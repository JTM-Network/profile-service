package com.jtmnetwork.profile.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Profile is already banned.")
class ProfileBanned: RuntimeException()