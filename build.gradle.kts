plugins {
    id("java")
    id("com.palantir.git-version") version "3.1.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("com.gradleup.shadow") version "8.3.6"
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
val physics = "0.1.0-c138a5d"
val paper = "1.20.6-R0.1-20240702.153951-123"

dependencies {
    testImplementation(platform("org.junit:junit-bom:${junit}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.wizardlybump17.physics:three:${physics}")
    implementation("com.wizardlybump17.physics:shared:${physics}")

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:${paper}")
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        expand(project.properties)
    }

    assemble {
        dependsOn(shadowJar)
    }
}