package com.jtmnetwork.profile.core.domain.model

import com.jtmnetwork.profile.core.util.UtilTime

data class Subscription(val start: Long = System.currentTimeMillis(), val level: Int, val age: String, val endTime: Long) {

    constructor(level: Int, age: String): this(level = level, age = age, endTime = UtilTime.parseAge(age))
}
