package com.jtmnetwork.profile.entrypoint.controller

import com.jtmnetwork.profile.core.domain.dto.AuthDTO
import com.jtmnetwork.profile.data.service.AuthService
import com.jtmnetwork.profile.data.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(private val authService: AuthService, private val tokenService: TokenService) {

    @PostMapping
    fun authenticate(@RequestBody dto: AuthDTO): Mono<Void> = authService.authenticate(dto.id, dto.plugin)

    @GetMapping("/{id}")
    fun getToken(@PathVariable id: String): Mono<String> = tokenService.getToken(id)
}