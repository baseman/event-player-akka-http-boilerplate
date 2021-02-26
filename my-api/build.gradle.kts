import co.remotectrl.ctrl.shell.cli.configureJUNITReports

repositories {
  jcenter()
}

plugins {
  kotlin("jvm")
  kotlin("plugin.spring") version "1.4.21"
  application
  id("org.springframework.boot") version "2.2.2.RELEASE"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

kotlin {
  sourceSets {
    main {
      dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(project(":event-ctrl"))
        implementation(project(":my-common"))

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.springframework.boot:spring-boot-starter")

        implementation("com.typesafe.akka:akka-http_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-http-jackson_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-stream_2.12:2.5.16")

        implementation("com.github.swagger-akka-http:swagger-akka-http_2.12:1.0.0")
        implementation("io.swagger:swagger-jaxrs:1.6.2")
        implementation("ch.megard:akka-http-cors_2.12:0.3.0")

        implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
        implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
      }
    }
    test {
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
  mainClassName = "co.remotectrl.myevent.api.ApplicationKt"
}

tasks.withType<Jar> {
  manifest {
    attributes["Main-Class"] = application.mainClassName
  }
  from(configurations.runtime.get().map { if (it.isDirectory) it else zipTree(it) })
}

configureJUNITReports(tasks)