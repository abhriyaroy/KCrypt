package studio.zebro.kcrypt

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform