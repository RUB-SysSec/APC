import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

application {
    mainClassName = "de.rub.mobsec.MainKt"
}

version = "1.0.3"

val junitVersion = "5.5.0"

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:1.6.0")

    api(kotlin("reflect"))
    
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-progressive")
}

tasks.withType<ShadowJar> {
    minimize()
    archiveBaseName.set("apc")
    archiveClassifier.set("")
    archiveVersion.set("")
}
