package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.data.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/token")
class TokenController @Autowired constructor(private val tokenService: TokenService) {

    @GetMapping("/generate")
    fun generate(req: ServerHttpRequest): Mono<String> = tokenService.generate(req)
}