plugins {
    id("java")
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "com.wizardlybump17.physics"
version = "${properties["version"]}-${gitVersion()}"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/WizardlyBump17/physics")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

val junit = "5.11.0"
val physics = "0.1.0-75889fc"

dependencies {
    testImplementation(platform("org.junit:junit-bom:${junit}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.wizardlybump17.physics:three:${physics}")
}

tasks {
    test {
        useJUnitPlatform()
    }
}