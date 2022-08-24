package com.jtmnetwork.profile.core.domain.entity

import com.devskiller.friendly_id.FriendlyId

data class Token(val id: String, val accountId: String, val token: String, var blacklisted: Boolean = false, val created: Long = System.currentTimeMillis()) {

    constructor(accountId: String, token: String): this(id = FriendlyId.createFriendlyId(), accountId = accountId, token = token)

    fun blacklist(): Token {
        this.blacklisted = true
        return this
    }
}