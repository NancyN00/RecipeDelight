package com.nancy.recipedelight

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform