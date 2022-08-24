package com.jtmnetwork.profile.core.usecase.provider

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider {

    @Value("\${jwt.secret-key:secretKey}")
    lateinit var secretKey: String

    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    fun createToken(accountId: String): String {
        val issuedAt = Date(System.currentTimeMillis())
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .setSubject(accountId)
            .setIssuedAt(issuedAt)
            .compact()
    }

    fun getAccountId(token: String): Optional<String> {
        return try {
            val claim = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            Optional.of(claim.body.subject)
        } catch (ex: Throwable) {
            Optional.empty()
        }
    }
}