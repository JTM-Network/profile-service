package com.jtmnetwork.profile.core.util

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.dto.PermissionDTO
import com.jtmnetwork.profile.core.domain.dto.SubDTO
import com.jtmnetwork.profile.core.domain.entity.Profile
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest

class TestUtil {
    companion object {
        fun createProfile(id: String): Profile {
            return Profile(id)
        }

        fun createHeaders(id: String?): HttpHeaders {
            val headers: HttpHeaders = mock()

            `when`(headers.getFirst("CLIENT_ID")).thenReturn(id)

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