package com.jtmnetwork.profile.core.util

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.dto.SubDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.domain.entity.Token
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.UUID

class TestUtil {
    companion object {
        fun createProfile(id: String): Profile {
            return Profile(id)
        }

        fun createToken(accountId: UUID): Token {
            return Token(accountId.toString(), "token")
        }

        fun createHeaders(id: String?): HttpHeaders {
            val headers: HttpHeaders = mock()

            `when`(headers.getFirst("CLIENT_ID")).thenReturn(id)
            `when`(headers.getFirst("PLUGIN_AUTHORIZATION")).thenReturn("Bearer token")

            return headers
        }

        fun createStripeHeaders(id: String): HttpHeaders {
            val headers: HttpHeaders = mock()

            `when`(headers.getFirst("CLIENT_ID")).thenReturn(id)
            `when`(headers.getFirst("Stripe-Signature")).thenReturn("sig")
            `when`(headers.getFirst("Stripe-Secret")).thenReturn("ev")

            return headers
        }

        fun createRequest(headers: HttpHeaders): ServerHttpRequest {
            val request: ServerHttpRequest = mock()

            `when`(request.headers).thenReturn(headers)

            return request
        }

        fun createSubDTO(): SubDTO {
            return SubDTO("plugin", 1, "30d")
        }

        fun createPermissionDTO(id: String): PermissionDTO {
            return PermissionDTO(id, "perms")
        }

        fun createPermissionDTO(id: String, permission: String): PermissionDTO {
            return PermissionDTO(id, permission)
        }

        fun assertProfile(returned: Profile, id: String) {
            assertThat(returned.id).isEqualTo(id)
            assertThat(returned.status).isEqualTo(AccountStatus.ONLINE)
        }

        fun assertBannedProfile(returned: Profile, id: String) {
            assertThat(returned.id).isEqualTo(id)
            assertThat(returned.status).isEqualTo(AccountStatus.BANNED)
        }

        fun assertUnbannedProfile(returned: Profile, id: String) {
            assertThat(returned.id).isEqualTo(id)
            assertThat(returned.status).isEqualTo(AccountStatus.OFFLINE)
        }
    }
}