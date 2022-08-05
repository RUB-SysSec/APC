plugins {
    base
    kotlin("jvm") version "1.7.10" apply false
}

allprojects {
    group = "de.rub.mobsec"

    repositories {
        mavenCentral()
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}