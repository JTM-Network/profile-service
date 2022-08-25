package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.AuthDTO
import com.jtmnetwork.profile.data.service.AuthService
import com.jtmnetwork.profile.data.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(private val authService: AuthService, private val tokenService: TokenService) {

    @GetMapping("/{id}")
    fun authenticate(req: ServerHttpRequest, @PathVariable id: String): Mono<Void> = authService.authenticate(req, id)

    @GetMapping("/token/{id}")
    fun getToken(@PathVariable id: String): Mono<String> = tokenService.getToken(id)
}