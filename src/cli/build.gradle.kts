plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
}

version = "1.0.5"

val junitVersion: String by project
val cliktVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt:clikt-core:$cliktVersion")

    api(kotlin("reflect"))
    
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    compilerOptions.progressiveMode.set(true)
}

// disables the normal jar task
tasks.jar {
    enabled = false
}

// and enables shadowJar task
artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    exclude("**/*.pro")
    manifest {
        attributes["Main-Class"] = "de.rub.mobsec.MainKt"
    }
    archiveBaseName.set("apc-shadow")
    archiveClassifier.set("")
    archiveVersion.set("")
    finalizedBy("proguard")
}

tasks.register<proguard.gradle.ProGuardTask>("proguard") {
    dependsOn(tasks.shadowJar)
    configuration("proguard-rules.pro")
    outputs.upToDateWhen { false }
}
