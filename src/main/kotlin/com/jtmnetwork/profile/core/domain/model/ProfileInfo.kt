package com.jtmnetwork.profile.core.domain.model

import java.util.Calendar
import java.util.Date

data class ProfileInfo(var username: String? = null, var dob: Date? = null) {

    fun updateUsername(username: String): ProfileInfo {
        this.username = username
        return this
    }

    fun updateDOB(year: Int, month: Int, day: Int): ProfileInfo {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        this.dob = cal.time
        return this
    }
}
