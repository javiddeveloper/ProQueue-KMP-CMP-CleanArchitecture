package xyz.sattar.javid.proqueue

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform