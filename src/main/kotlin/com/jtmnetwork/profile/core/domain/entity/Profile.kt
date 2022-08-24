package com.jtmnetwork.profile.core.domain.entity

import com.jtmnetwork.profile.core.domain.constants.AccountStatus
import com.jtmnetwork.profile.core.domain.model.Subscription
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("profiles")
data class Profile(@Id val id: String,
                   val subs: MutableMap<String, Subscription> = HashMap(),
                   var status: AccountStatus,
                   val permissions: MutableList<String> = ArrayList(),
                   var updated: Long = System.currentTimeMillis(),
                   val joined: Long = System.currentTimeMillis()) {

    constructor(id: String): this(id = id, status = AccountStatus.ONLINE)

    fun addSubscription(name: String, level: Int, age: String): Profile {
        this.subs[name] = Subscription(level, age)
        this.updated()
        return this
    }

    fun hasSubscription(name: String): Boolean {
        return this.subs.containsKey(name)
    }

    fun removeSubscription(name: String): Profile {
        this.subs.remove(name)
        this.updated()
        return this
    }

    fun addPermission(permissions: Array<String>): Profile {
        permissions.forEach { if (!this.permissions.contains(it)) this.permissions.add(it) }
        this.updated()
        return this
    }

    fun addPermission(permission: String): Profile {
        this.permissions.add(permission)
        this.updated()
        return this
    }

    fun hasPermission(permission: String): Boolean {
        return this.permissions.contains(permission)
    }

    fun removePermission(permission: String): Profile {
        this.permissions.remove(permission)
        this.updated()
        return this
    }

    fun isBanned(): Boolean {
        return status == AccountStatus.BANNED
    }

    fun ban(): Profile {
        this.status = AccountStatus.BANNED
        this.updated()
        return this
    }

    fun unban(): Profile {
        this.status = AccountStatus.OFFLINE
        this.updated()
        return this
    }

    private fun updated() {
        this.updated = System.currentTimeMillis()
    }
}
