package com.jtmnetwork.profile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
open class ProfileApplication

fun main(args: Array<String>) {
    runApplication<ProfileApplication>(*args)
}