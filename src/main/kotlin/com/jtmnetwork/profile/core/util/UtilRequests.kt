package com.jtmnetwork.profile.core.util

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono
import java.nio.ByteBuffer

class UtilRequests {
    companion object {
        fun getBodyAsString(request: ServerHttpRequest): Mono<String> {
            return DataBufferUtils.join(request.body)
                .map(DataBuffer::asByteBuffer)
                .map(ByteBuffer::array)
                .map { String() }
        }
    }
}