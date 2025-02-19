plugins {
    id("java")
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "com.wizardlybump17.physics"
version = "${properties["version"]}-${gitVersion()}"

repositories {
    mavenCentral()
}

val junit = "5.11.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:${junit}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}