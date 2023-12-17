buildscript {
    repositories {
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        google()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    }
}

plugins {
    id("com.android.application") version "8.2.0-beta05" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
