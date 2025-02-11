// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    
    // Disable state tracking for all Google Services plugin tasks
    tasks.withType<com.google.gms.googleservices.GoogleServicesTask>().configureEach {
        doNotTrackState("Required to fix outputDirectory access issue")
        notCompatibleWithConfigurationCache("Temporary workaround for configuration cache")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}