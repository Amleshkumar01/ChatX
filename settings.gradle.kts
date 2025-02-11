pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.0")
            version("agp", "8.2.0")
            
            plugin("android-application", "com.android.application").version("${version("agp")}")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").version("${version("kotlin")}")
            
            library("androidx-core-ktx", "androidx.core:core-ktx:1.12.0")
            library("androidx-appcompat", "androidx.appcompat:appcompat:1.6.1")
            library("material", "com.google.android.material:material:1.11.0")
            library("androidx-constraintlayout", "androidx.constraintlayout:constraintlayout:2.1.4")
        }
    }
}

rootProject.name = "ChatX"
include(":app")
