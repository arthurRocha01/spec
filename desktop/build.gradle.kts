plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

group = "com.spec"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Compose Desktop
    implementation(compose.material3)
    implementation(compose.desktop.currentOs)

    // HTTP Client
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)

    // Images
    implementation(libs.coil.compose)
}

compose.desktop {
    application {
        mainClass = "com.spec.desktop.MainKt"
    }
}
