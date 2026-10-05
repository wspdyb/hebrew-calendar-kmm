plugins {
    kotlin("multiplatform") version "1.9.21"
    id("com.android.library") version "8.1.4"
}

kotlin {
    // Android target
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    // iOS targets
    ios()
    iosSimulatorArm64()

    // JVM for desktop
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // No external dependencies for core calendar engine
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting
        val androidUnitTest by getting

        val iosMain by getting
        val iosTest by getting

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        val desktopMain by getting {
            dependsOn(commonMain)
        }
        val desktopTest by getting {
            dependsOn(commonTest)
        }
    }
}

android {
    namespace = "com.example.hebrewcalendar"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}
