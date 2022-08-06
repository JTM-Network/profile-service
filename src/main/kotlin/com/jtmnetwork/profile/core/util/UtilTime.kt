package com.jtmnetwork.profile.core.util

class UtilTime {
    companion object {

        // TODO: Valid check age with correct suffix
        fun validAge(age: String): Boolean {
            return false
        }

        // TODO: parse the age given e.g. 30d, 1h, 24h,
        fun parseAge(age: String): Long {
            return System.currentTimeMillis()
        }
    }
}