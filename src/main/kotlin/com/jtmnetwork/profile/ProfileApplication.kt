package com.jtmnetwork.profile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ProfileApplication

fun main(args: Array<String>) {
    runApplication<ProfileApplication>(*args)
}