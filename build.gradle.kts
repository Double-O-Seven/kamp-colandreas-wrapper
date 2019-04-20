import ch.leadrian.samp.kamp.gradle.plugin.pluginwrappergenerator.PluginWrapperGeneratorExtension
import com.google.common.base.CaseFormat
import groovy.lang.Closure

buildscript {
    dependencies {
        repositories {
            mavenCentral()
            mavenLocal()
            maven {
                setUrl("https://plugins.gradle.org/m2/")
            }
        }

        classpath(
                group = "ch.leadrian.samp.kamp",
                name = "kamp-plugin-wrapper-generator",
                version = "1.0.0-rc2"
        )
    }
}

plugins {
    kotlin("jvm") version "1.3.11"
    kotlin("kapt") version "1.3.11"
    `java-library`
    `maven-publish`
    maven
    signing
    `build-scan`
    jacoco
    id("org.jetbrains.dokka") version "0.9.17"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

apply(plugin = "kamp-plugin-wrapper-generator")

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        setUrl("https://dl.bintray.com/spekframework/spek")
    }
}

jacoco {
    toolVersion = "0.8.3"
}

configure<PluginWrapperGeneratorExtension> {
    packageName = "ch.leadrian.samp.kamp.colAndreaswrapper"
    pluginName = "ColAndreas"
    removePrefix("CA_")
    nativeFunctionsCaseFormat = CaseFormat.UPPER_CAMEL
    callbacksCaseFormat = CaseFormat.UPPER_CAMEL
    interfaceDefintionFile(project.projectDir.resolve("src/main/idl/ColAndreas.idl"))
}

dependencies {
    val kampVersion = "1.0.0-rc7"

    api(group = "ch.leadrian.samp.kamp", name = "kamp-core", version = kampVersion)
    api(group = "ch.leadrian.samp.kamp", name = "kamp-annotations", version = kampVersion)

    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = "1.3.11")
    api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = "1.3.11")
    api(group = "com.google.guava", name = "guava", version = "27.0.1-jre")
    api(group = "com.google.inject", name = "guice", version = "4.2.2")
    api(group = "com.netflix.governator", name = "governator", version = "1.17.5")
    api(group = "javax.inject", name = "javax.inject", version = "1")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.4.0")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.12.1")
    testImplementation(group = "io.mockk", name = "mockk", version = "1.9.1")
    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = "2.0.1")

    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = "2.0.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.4.0")

    kapt(group = "ch.leadrian.samp.kamp", name = "kamp-annotation-processor", version = kampVersion)
}

val gitVersion: Closure<String> by extra

version = gitVersion()

group = "ch.leadrian.samp.kamp"

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.dokka)
    archiveClassifier.set("javadoc")
}

tasks {
    compileKotlin {
        sourceCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=compatibility")
        }
    }

    compileTestKotlin {
        sourceCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=compatibility")
        }
    }

    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    jacocoTestReport {
        dependsOn(test)
    }

    dokka {
        reportUndocumented = false
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Kamp ColAndreas Wrapper")
                description.set("Kotlin API for native SA-MP plugin ColAndreas")
                url.set("https://github.com/Double-O-Seven/kamp-colandreas-wrapper")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Double-O-Seven")
                        name.set("Adrian-Philipp Leuenberger")
                        email.set("thewishwithin@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Double-O-Seven/kamp-colandreas-wrapper.git")
                    developerConnection.set("scm:git:ssh://github.com/Double-O-Seven/kamp-colandreas-wrapper.git")
                    url.set("https://github.com/Double-O-Seven/kamp-colandreas-wrapper")
                }
            }
        }
    }
    repositories {
        maven {
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            url = if (version.toString().contains("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                val ossrhUsername: String? by extra
                val ossrhPassword: String? by extra
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}
