package com.jtmnetwork.profile.core.domain.entity

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.model.Subscription
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("profiles")
data class Profile(@Id val id: String, val subs: MutableMap<String, Subscription> = HashMap(),
                   var status: AccountStatus,
                   val permissions: MutableList<String> = ArrayList(),
                   var updated: Long = System.currentTimeMillis(),
                   val joined: Long = System.currentTimeMillis()) {

    constructor(id: String): this(id = id, status = AccountStatus.ONLINE)

    fun isBanned(): Boolean {
        return status == AccountStatus.BANNED
    }

    fun ban(): Profile {
        this.status = AccountStatus.BANNED
        this.updated()
        return this
    }

    fun updated() {
        this.updated = System.currentTimeMillis()
    }
}
