package com.jtmnetwork.profile.core.util

import java.util.concurrent.TimeUnit

class UtilTime {
    companion object {

        fun validAge(age: String): Boolean {
            val suffix = age.substring(age.length - 1)
            return suffix.contentEquals("h") || suffix.contentEquals("d") || suffix.contentEquals("m") || suffix.contentEquals("y")
        }

        fun parseAge(age: String): Long {
            if (!validAge(age)) return 0
            val suffix = age.substring(age.length - 1)
            val time = age.substring(0, age.length - 1).toLongOrNull() ?: return 0
            return when(suffix) {
                "h" -> System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time)
                "d" -> System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time)
                "m" -> System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time * 30)
                "y" -> System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time * 365)
                else -> 0
            }
        }
    }
}