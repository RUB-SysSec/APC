plugins {
    base
    kotlin("jvm") version "1.3.41" apply false
}

allprojects {
    group = "de.rub.mobsec"

    repositories {
        jcenter()
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}