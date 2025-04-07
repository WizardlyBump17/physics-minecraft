plugins {
    id("java")
    id("com.palantir.git-version") version "3.1.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("com.gradleup.shadow") version "8.3.6"
    id("maven-publish")
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "com.wizardlybump17.physics"
version = "${properties["version"]}-${gitVersion()}"

println("Running $name $version")

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/WizardlyBump17/physics")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
    mavenLocal()
}

val junit = "5.11.0"
val paper = "1.20.6-R0.1-20240702.153951-123"

dependencies {
    testImplementation(platform("org.junit:junit-bom:${junit}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.wizardlybump17.physics:three")
    implementation("com.wizardlybump17.physics:shared")

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

    java {
        withSourcesJar()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/WizardlyBump17/physics-minecraft")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("github-packages") {
            from(components["java"])
            artifacts {
                tasks.named("reobfJar")
            }
        }
    }
}