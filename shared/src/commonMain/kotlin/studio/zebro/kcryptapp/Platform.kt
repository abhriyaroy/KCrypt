package studio.zebro.kcryptapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform