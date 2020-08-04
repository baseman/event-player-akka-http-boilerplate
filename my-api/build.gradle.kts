import co.remotectrl.ctrl.shell.cli.configureJUNITReports
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
  jcenter()
}

plugins {
  kotlin("jvm")
  kotlin("plugin.spring") version "1.4.0-rc"
  application
  id("org.springframework.boot") version "2.2.2.RELEASE"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
  id("com.github.johnrengelman.shadow") version "4.0.4"
}

kotlin {
  sourceSets {
    val main by getting {
      dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(project(":event-player"))
        implementation(project(":my-common"))

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.springframework.boot:spring-boot-starter")

        implementation("com.typesafe.akka:akka-http_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-http-jackson_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-stream_2.12:2.5.16")

        implementation("com.github.swagger-akka-http:swagger-akka-http_2.12:1.0.0")
        implementation("io.swagger:swagger-jaxrs:1.5.18")
        implementation("ch.megard:akka-http-cors_2.12:0.3.0")
      }
    }
    val test by getting {
      dependencies {
        implementation(kotlin("test-junit"))

        implementation("com.typesafe.akka:akka-http-testkit_2.12:10.1.4")

        implementation("org.springframework.boot:spring-boot-starter-test:2.0.5.RELEASE")
      }
    }
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

application {
  mainClassName = "co.remotectrl.ctrl.shell.application.CtrlShellKt"
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveBaseName.set("ctrl-shell")
    mergeServiceFiles()
    manifest {
      attributes(mapOf("Main-Class" to application.mainClassName))
    }
  }
}

tasks {
  build {
    dependsOn(shadowJar)
  }
}

configureJUNITReports(tasks)