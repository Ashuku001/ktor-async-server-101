package com.example.plugins

import com.example.plugins.di.configureDI
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    dao.DatabaseFactory.init()
    configureDI()
    configureSecurity()
    configureRouting()
}
