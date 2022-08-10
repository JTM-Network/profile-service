package com.jtmnetwork.profile.core.util

class UtilString {
    companion object {
        fun stringToPlugins(plugins: String): Array<String> {
            val split = plugins.split(",")
            return split.toTypedArray()
        }
    }
}